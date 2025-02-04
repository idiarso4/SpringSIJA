package com.school.security.controller;

import com.school.security.dto.PasswordResetRequest;
import com.school.security.dto.PasswordUpdateRequest;
import com.school.security.service.PasswordResetService;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.responses.ApiResponse;
import io.swagger.v3.oas.annotations.tags.Tag;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequestMapping("/api/v1/password")
@RequiredArgsConstructor
@Tag(name = "Password Reset", description = "Password reset and update endpoints")
public class PasswordResetController {

    private final PasswordResetService passwordResetService;

    @PostMapping("/reset-request")
    @Operation(
        summary = "Request password reset",
        description = "Send a password reset link to the user's email"
    )
    @ApiResponse(
        responseCode = "200",
        description = "Password reset email sent successfully"
    )
    public ResponseEntity<Void> requestPasswordReset(
        @Valid @RequestBody PasswordResetRequest request
    ) {
        passwordResetService.requestPasswordReset(request);
        return ResponseEntity.ok().build();
    }

    @PostMapping("/reset")
    @Operation(
        summary = "Reset password",
        description = "Reset password using the token received via email"
    )
    @ApiResponse(
        responseCode = "200",
        description = "Password reset successful"
    )
    public ResponseEntity<Void> resetPassword(
        @Valid @RequestBody PasswordUpdateRequest request
    ) {
        passwordResetService.resetPassword(request);
        return ResponseEntity.ok().build();
    }
}
