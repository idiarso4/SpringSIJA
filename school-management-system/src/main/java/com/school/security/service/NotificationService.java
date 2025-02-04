package com.school.security.service;

import com.school.masterdata.entity.User;
import lombok.RequiredArgsConstructor;
import org.springframework.mail.SimpleMailMessage;
import org.springframework.mail.javamail.JavaMailSender;
import org.springframework.stereotype.Service;

@Service
@RequiredArgsConstructor
public class NotificationService {
    
    private final JavaMailSender mailSender;

    public void sendPasswordResetNotification(String email, String resetToken) {
        SimpleMailMessage message = new SimpleMailMessage();
        message.setTo(email);
        message.setSubject("Password Reset Request");
        message.setText("To reset your password, click the following link: "
                + "http://localhost:8080/reset-password?token=" + resetToken);
        mailSender.send(message);
    }

    public void sendPermissionApprovalNotification(User user, String message) {
        SimpleMailMessage mailMessage = new SimpleMailMessage();
        mailMessage.setTo(user.getEmail());
        mailMessage.setSubject("Permission Request Approved");
        mailMessage.setText(message);
        mailSender.send(mailMessage);
    }

    public void sendPermissionRejectionNotification(User user, String message) {
        SimpleMailMessage mailMessage = new SimpleMailMessage();
        mailMessage.setTo(user.getEmail());
        mailMessage.setSubject("Permission Request Rejected");
        mailMessage.setText(message);
        mailSender.send(mailMessage);
    }
}
