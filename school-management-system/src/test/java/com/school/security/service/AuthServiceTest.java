package com.school.security.service;

import com.school.masterdata.entity.Student;
import com.school.masterdata.entity.Teacher;
import com.school.masterdata.entity.User;
import com.school.masterdata.repository.StudentRepository;
import com.school.masterdata.repository.TeacherRepository;
import com.school.masterdata.repository.UserRepository;
import com.school.security.dto.AuthRequest;
import com.school.security.dto.AuthResponse;
import com.school.security.dto.RegisterRequest;
import com.school.security.jwt.JwtService;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.security.authentication.BadCredentialsException;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.core.userdetails.UsernameNotFoundException;
import org.springframework.security.crypto.password.PasswordEncoder;

import java.time.LocalDate;
import java.util.HashSet;
import java.util.Optional;
import java.util.Set;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
class AuthServiceTest {

    @Mock
    private UserRepository userRepository;

    @Mock
    private TeacherRepository teacherRepository;

    @Mock
    private StudentRepository studentRepository;

    @Mock
    private PasswordEncoder passwordEncoder;

    @Mock
    private JwtService jwtService;

    @Mock
    private CustomUserDetailsService userDetailsService;

    @InjectMocks
    private AuthService authService;

    private User user;
    private Teacher teacher;
    private Student student;
    private RegisterRequest registerRequest;
    private UserDetails userDetails;
    private String token;

    @BeforeEach
    void setUp() {
        user = new User();
        user.setId(1L);
        user.setUsername("testuser");
        user.setPassword("encodedPassword");
        user.setEmail("test@example.com");
        user.setName("Test User");
        Set<String> roles = new HashSet<>();
        roles.add("TEACHER");
        user.setRoles(roles);

        teacher = new Teacher();
        teacher.setId(1L);
        teacher.setUser(user);
        teacher.setTeacherNumber("T123");

        student = new Student();
        student.setId(1L);
        student.setUser(user);
        student.setStudentNumber("S123");
        student.setBirthDate(LocalDate.now());
        student.setEntryYear(2023);

        registerRequest = new RegisterRequest();
        registerRequest.setUsername("testuser");
        registerRequest.setPassword("password123");
        registerRequest.setEmail("test@example.com");
        registerRequest.setName("Test User");
        registerRequest.setRole("TEACHER");
        registerRequest.setIdentificationNumber("T123");

        userDetails = org.springframework.security.core.userdetails.User.builder()
                .username("testuser")
                .password("encodedPassword")
                .authorities("ROLE_TEACHER")
                .build();

        token = "testToken";
    }

    @Test
    void register_Teacher_Success() {
        when(passwordEncoder.encode(any())).thenReturn("encodedPassword");
        Teacher teacher = new Teacher();
        teacher.setId(1L);
        teacher.setTeacherNumber(registerRequest.getIdentificationNumber());
        teacher.setSpecialization("Mathematics");
        when(teacherRepository.save(any(Teacher.class))).thenReturn(teacher);
        when(userRepository.save(any())).thenReturn(user);
        when(userDetailsService.loadUserByUsername(any())).thenReturn(userDetails);
        when(jwtService.generateToken(any(UserDetails.class))).thenReturn(token);

        AuthResponse response = authService.register(registerRequest);

        assertNotNull(response);
        assertEquals(token, response.getToken());
        assertEquals("testuser", response.getUsername());
        assertEquals("Test User", response.getFullName());
        assertEquals("TEACHER", response.getUserType());
        verify(userRepository).save(any(User.class));
        verify(teacherRepository).save(any(Teacher.class));
    }

    @Test
    void authenticateTeacher_Success() {
        when(userRepository.findByUsername("testuser")).thenReturn(Optional.of(user));
        when(passwordEncoder.matches(any(), any())).thenReturn(true);
        when(teacherRepository.findByUser(user)).thenReturn(Optional.of(teacher));
        when(userDetailsService.loadUserByUsername(any())).thenReturn(userDetails);
        when(jwtService.generateToken(any(UserDetails.class))).thenReturn(token);

        AuthResponse response = authService.authenticateTeacher(new AuthRequest("testuser", "password123"));

        assertNotNull(response);
        assertEquals(token, response.getToken());
        assertEquals("testuser", response.getUsername());
        assertEquals("Test User", response.getFullName());
        assertEquals("TEACHER", response.getUserType());
    }

    @Test
    void authenticateTeacher_InvalidCredentials() {
        when(userRepository.findByUsername("testuser")).thenReturn(Optional.of(user));
        when(passwordEncoder.matches(any(), any())).thenReturn(false);

        assertThrows(BadCredentialsException.class, () ->
                authService.authenticateTeacher(new AuthRequest("testuser", "wrongpassword")));
    }

    @Test
    void authenticateTeacher_UserNotFound() {
        when(userRepository.findByUsername("nonexistent")).thenReturn(Optional.empty());

        assertThrows(UsernameNotFoundException.class, () ->
                authService.authenticateTeacher(new AuthRequest("nonexistent", "password")));
    }

    @Test
    void authenticateStudent_Success() {
        when(userRepository.findByUsername("testuser")).thenReturn(Optional.of(user));
        when(passwordEncoder.matches(any(), any())).thenReturn(true);
        when(studentRepository.findByUser(user)).thenReturn(Optional.of(student));
        when(userDetailsService.loadUserByUsername(any())).thenReturn(userDetails);
        when(jwtService.generateToken(any(UserDetails.class))).thenReturn(token);

        AuthResponse response = authService.authenticateStudent(new AuthRequest("testuser", "password123"));

        assertNotNull(response);
        assertEquals(token, response.getToken());
        assertEquals("testuser", response.getUsername());
        assertEquals("Test User", response.getFullName());
        assertEquals("STUDENT", response.getUserType());
    }
}
