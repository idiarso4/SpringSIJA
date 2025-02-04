package com.school.security.service;

import com.school.masterdata.entity.Student;
import com.school.masterdata.entity.Teacher;
import com.school.masterdata.entity.User;
import com.school.masterdata.repository.StudentRepository;
import com.school.masterdata.repository.TeacherRepository;
import com.school.masterdata.repository.UserRepository;
import com.school.security.dto.AuthResponse;
import com.school.security.dto.LoginRequest;
import com.school.security.dto.RegisterRequest;
import com.school.security.entity.Role;
import com.school.security.jwt.JwtService;
import lombok.RequiredArgsConstructor;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.Collections;
import java.util.HashSet;

@Service
@RequiredArgsConstructor
public class AuthService {

    private final UserRepository userRepository;
    private final StudentRepository studentRepository;
    private final TeacherRepository teacherRepository;
    private final PasswordEncoder passwordEncoder;
    private final JwtService jwtService;
    private final AuthenticationManager authenticationManager;

    @Transactional
    public AuthResponse register(RegisterRequest request) {
        User user = new User();
        user.setUsername(request.getUsername());
        user.setPassword(passwordEncoder.encode(request.getPassword()));
        user.setEmail(request.getEmail());
        user.setFullName(request.getName());
        user.setRole(request.getRole());
        user = userRepository.save(user);

        if (request.getRole() == Role.STUDENT) {
            Student student = new Student();
            student.setUser(user);
            student.setIdentificationNumber(request.getIdentificationNumber());
            student.setBirthDate(request.getBirthDate());
            student.setEntryYear(request.getEntryYear());
            studentRepository.save(student);
        } else if (request.getRole() == Role.TEACHER) {
            Teacher teacher = new Teacher();
            teacher.setUser(user);
            teacher.setIdentificationNumber(request.getIdentificationNumber());
            teacherRepository.save(teacher);
        }

        String jwt = jwtService.generateToken(user);

        return AuthResponse.builder()
                .token(jwt)
                .username(user.getUsername())
                .email(user.getEmail())
                .fullName(user.getFullName())
                .role(user.getRole())
                .build();
    }

    public AuthResponse login(LoginRequest request) {
        Authentication authentication = authenticationManager.authenticate(
                new UsernamePasswordAuthenticationToken(request.getUsername(), request.getPassword())
        );

        User user = userRepository.findByUsername(request.getUsername())
                .orElseThrow(() -> new RuntimeException("User not found"));

        String jwt = jwtService.generateToken(user);

        return AuthResponse.builder()
                .token(jwt)
                .username(user.getUsername())
                .email(user.getEmail())
                .fullName(user.getFullName())
                .role(user.getRole())
                .build();
    }
}
