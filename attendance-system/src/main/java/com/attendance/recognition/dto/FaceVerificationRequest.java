package com.attendance.recognition.dto;

import jakarta.validation.constraints.NotNull;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;
import org.springframework.web.multipart.MultipartFile;

@Data
@NoArgsConstructor
@AllArgsConstructor
public class FaceVerificationRequest {

    @NotNull(message = "Student ID is required")
    private Long studentId;

    @NotNull(message = "Face image is required")
    private MultipartFile faceImage;

    private Boolean requireLiveness = true;
}
