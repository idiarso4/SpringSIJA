package com.school.security.service;

import com.school.masterdata.entity.User;
import com.school.masterdata.repository.UserRepository;
import com.school.security.dto.PasswordResetRequest;
import com.school.security.dto.PasswordUpdateRequest;
import com.school.security.service.NotificationService;
import jakarta.persistence.EntityNotFoundException;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.security.crypto.password.PasswordEncoder;

import java.util.Optional;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.eq;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
class PasswordResetServiceTest {

    @Mock
    private UserRepository userRepository;

    @Mock
    private PasswordEncoder passwordEncoder;

    @Mock
    private NotificationService notificationService;

    @InjectMocks
    private PasswordResetService passwordResetService;

    private User user;
    private PasswordResetRequest resetRequest;
    private PasswordUpdateRequest updateRequest;
    private String token;

    @BeforeEach
    void setUp() {
        user = new User();
        user.setId(1L);
        user.setUsername("testuser");
        user.setPassword("oldPassword");
        user.setEmail("test@example.com");

        token = "validToken";

        resetRequest = new PasswordResetRequest();
        resetRequest.setEmail("test@example.com");

        updateRequest = new PasswordUpdateRequest();
        updateRequest.setToken(token);
        updateRequest.setNewPassword("newPassword");
        updateRequest.setConfirmPassword("newPassword");
    }

    @Test
    void initiatePasswordReset_Success() {
        when(userRepository.findByEmail("test@example.com")).thenReturn(Optional.of(user));
        when(userRepository.save(any(User.class))).thenReturn(user);

        passwordResetService.initiatePasswordReset(resetRequest);

        verify(userRepository).save(user);
        assertNotNull(user.getResetToken());
        verify(notificationService).sendPasswordResetNotification(eq("test@example.com"), any());
    }

    @Test
    void initiatePasswordReset_UserNotFound() {
        when(userRepository.findByEmail("test@example.com")).thenReturn(Optional.empty());

        assertThrows(EntityNotFoundException.class, () -> 
            passwordResetService.initiatePasswordReset(resetRequest)
        );

        verify(userRepository, never()).save(any());
        verify(notificationService, never()).sendPasswordResetNotification(any(), any());
    }

    @Test
    void updatePassword_Success() {
        when(userRepository.findByResetToken(token)).thenReturn(Optional.of(user));
        when(passwordEncoder.encode(any())).thenReturn("encodedNewPassword");

        passwordResetService.updatePassword(updateRequest);

        verify(userRepository).save(user);
        assertEquals("encodedNewPassword", user.getPassword());
        assertNull(user.getResetToken());
    }

    @Test
    void updatePassword_InvalidToken() {
        when(userRepository.findByResetToken(token)).thenReturn(Optional.empty());

        assertThrows(EntityNotFoundException.class, () -> 
            passwordResetService.updatePassword(updateRequest)
        );

        verify(userRepository, never()).save(any());
    }

    @Test
    void updatePassword_PasswordMismatch() {
        updateRequest.setConfirmPassword("differentPassword");

        IllegalArgumentException exception = assertThrows(IllegalArgumentException.class, () ->
            passwordResetService.updatePassword(updateRequest)
        );

        assertEquals("Password and confirm password do not match", exception.getMessage());
        verify(userRepository, never()).findByResetToken(any());
        verify(userRepository, never()).save(any());
    }
}
