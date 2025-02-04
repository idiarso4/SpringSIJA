package com.school.security.service;

import com.school.masterdata.entity.User;
import com.school.masterdata.repository.UserRepository;
import com.school.security.dto.PasswordResetRequest;
import com.school.security.dto.PasswordUpdateRequest;
import com.school.common.service.NotificationService;
import jakarta.persistence.EntityNotFoundException;
import lombok.RequiredArgsConstructor;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.UUID;

@Service
@RequiredArgsConstructor
public class PasswordResetService {

    private final UserRepository userRepository;
    private final PasswordEncoder passwordEncoder;
    private final NotificationService notificationService;

    @Transactional
    public void initiatePasswordReset(PasswordResetRequest request) {
        User user = userRepository.findByEmail(request.getEmail())
                .orElseThrow(() -> new EntityNotFoundException("User not found"));

        String resetToken = UUID.randomUUID().toString();
        user.setResetToken(resetToken);
        userRepository.save(user);

        notificationService.sendPasswordResetNotification(user.getEmail(), resetToken);
    }

    @Transactional
    public void updatePassword(PasswordUpdateRequest request) {
        if (!request.getNewPassword().equals(request.getConfirmPassword())) {
            throw new IllegalArgumentException("Password and confirm password do not match");
        }

        User user = userRepository.findByResetToken(request.getToken())
                .orElseThrow(() -> new EntityNotFoundException("Invalid or expired reset token"));

        user.setPassword(passwordEncoder.encode(request.getNewPassword()));
        user.setResetToken(null);
        userRepository.save(user);
    }
}
