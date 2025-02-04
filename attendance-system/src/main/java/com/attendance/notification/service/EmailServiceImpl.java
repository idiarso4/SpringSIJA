package com.attendance.notification.service;

import jakarta.mail.MessagingException;
import jakarta.mail.internet.MimeMessage;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.mail.javamail.JavaMailSender;
import org.springframework.mail.javamail.MimeMessageHelper;
import org.springframework.scheduling.annotation.Async;
import org.springframework.stereotype.Service;
import org.thymeleaf.TemplateEngine;
import org.thymeleaf.context.Context;

import java.util.Map;

@Slf4j
@Service
@RequiredArgsConstructor
public class EmailServiceImpl implements EmailService {

    private final JavaMailSender mailSender;
    private final TemplateEngine templateEngine;

    @Value("${spring.mail.username}")
    private String fromEmail;

    @Value("${app.frontend-url}")
    private String frontendUrl;

    @Override
    @Async
    public void sendPasswordResetEmail(String toEmail, String token) {
        Context context = new Context();
        context.setVariables(Map.of(
            "resetLink", frontendUrl + "/reset-password?token=" + token,
            "supportEmail", fromEmail
        ));

        String emailContent = templateEngine.process("password-reset", context);
        sendHtmlEmail(toEmail, "Reset Your Password", emailContent);
    }

    @Override
    @Async
    public void sendWelcomeEmail(String toEmail, String fullName) {
        Context context = new Context();
        context.setVariables(Map.of(
            "fullName", fullName,
            "loginLink", frontendUrl + "/login",
            "supportEmail", fromEmail
        ));

        String emailContent = templateEngine.process("welcome", context);
        sendHtmlEmail(toEmail, "Welcome to Attendance System", emailContent);
    }

    @Override
    @Async
    public void sendAttendanceConfirmation(String toEmail, String fullName, String date, String time, String location) {
        Context context = new Context();
        context.setVariables(Map.of(
            "fullName", fullName,
            "date", date,
            "time", time,
            "location", location
        ));

        String emailContent = templateEngine.process("attendance-confirmation", context);
        sendHtmlEmail(toEmail, "Attendance Confirmation", emailContent);
    }

    @Override
    @Async
    public void sendPKLActivityApproval(String toEmail, String studentName, String date, String supervisorName, String notes) {
        Context context = new Context();
        context.setVariables(Map.of(
            "studentName", studentName,
            "date", date,
            "supervisorName", supervisorName,
            "notes", notes,
            "activityLink", frontendUrl + "/pkl/activities"
        ));

        String emailContent = templateEngine.process("pkl-activity-approval", context);
        sendHtmlEmail(toEmail, "PKL Activity Approved", emailContent);
    }

    private void sendHtmlEmail(String to, String subject, String htmlContent) {
        try {
            MimeMessage message = mailSender.createMimeMessage();
            MimeMessageHelper helper = new MimeMessageHelper(message, true, "UTF-8");
            
            helper.setFrom(fromEmail);
            helper.setTo(to);
            helper.setSubject(subject);
            helper.setText(htmlContent, true);
            
            mailSender.send(message);
            log.info("Email sent successfully to: {}", to);
        } catch (MessagingException e) {
            log.error("Failed to send email to: {}", to, e);
            throw new RuntimeException("Failed to send email", e);
        }
    }
}
