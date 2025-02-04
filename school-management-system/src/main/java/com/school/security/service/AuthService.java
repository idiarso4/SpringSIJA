package com.school.security.service;

import com.school.masterdata.entity.Role;
import com.school.masterdata.entity.Student;
import com.school.masterdata.entity.Teacher;
import com.school.masterdata.entity.User;
import com.school.masterdata.repository.RoleRepository;
import com.school.masterdata.repository.StudentRepository;
import com.school.masterdata.repository.TeacherRepository;
import com.school.masterdata.repository.UserRepository;
import com.school.security.dto.AuthRequest;
import com.school.security.dto.AuthResponse;
import com.school.security.dto.RegisterRequest;
import lombok.RequiredArgsConstructor;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

@Service
@RequiredArgsConstructor
public class AuthService {
    private final UserRepository userRepository;
    private final RoleRepository roleRepository;
    private final StudentRepository studentRepository;
    private final TeacherRepository teacherRepository;
    private final PasswordEncoder passwordEncoder;
    private final JwtService jwtService;
    private final AuthenticationManager authenticationManager;

    @Transactional
    public AuthResponse register(RegisterRequest request) {
        Role role = roleRepository.findByRole(Role.UserRole.valueOf(request.getRole()))
                .orElseThrow(() -> new IllegalArgumentException("Invalid role"));

        User user = new User();
        user.setUsername(request.getUsername());
        user.setPassword(passwordEncoder.encode(request.getPassword()));
        user.setEmail(request.getEmail());
        user.setName(request.getName());
        user.setRole(role);

        if (role.getRole() == Role.UserRole.STUDENT) {
            Student student = new Student();
            student.setUser(user);
            student.setIdentificationNumber(request.getIdentificationNumber());
            student.setBirthDate(request.getBirthDate());
            student.setEntryYear(request.getEntryYear());
            studentRepository.save(student);
        } else if (role.getRole() == Role.UserRole.TEACHER) {
            Teacher teacher = new Teacher();
            teacher.setUser(user);
            teacher.setIdentificationNumber(request.getIdentificationNumber());
            teacherRepository.save(teacher);
        }

        user = userRepository.save(user);
        String jwtToken = jwtService.generateToken(user);

        return AuthResponse.builder()
                .token(jwtToken)
                .username(user.getUsername())
                .role(user.getRoles().name())
                .build();
    }

    public AuthResponse authenticate(AuthRequest request) {
        authenticationManager.authenticate(
                new UsernamePasswordAuthenticationToken(
                        request.getUsername(),
                        request.getPassword()
                )
        );

        User user = userRepository.findByUsername(request.getUsername())
                .orElseThrow(() -> new IllegalArgumentException("User not found"));

        String jwtToken = jwtService.generateToken(user);

        return AuthResponse.builder()
                .token(jwtToken)
                .username(user.getUsername())
                .role(user.getRoles().name())
                .build();
    }
}
