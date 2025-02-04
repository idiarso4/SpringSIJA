package com.attendance.recognition.controller;

import com.attendance.recognition.dto.FaceRegistrationRequest;
import com.attendance.recognition.dto.FaceVerificationRequest;
import com.attendance.recognition.model.FaceMatchResult;
import com.attendance.recognition.service.FaceRecognitionService;
import com.attendance.security.dto.MessageResponse;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.tags.Tag;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.multipart.MultipartFile;

@RestController
@RequestMapping("/api/v1/face")
@RequiredArgsConstructor
@Tag(name = "Face Recognition", description = "Face recognition APIs")
public class FaceRecognitionController {

    private final FaceRecognitionService faceRecognitionService;

    @PostMapping(value = "/register", consumes = MediaType.MULTIPART_FORM_DATA_VALUE)
    @PreAuthorize("hasAnyRole('STUDENT', 'ADMIN')")
    @Operation(summary = "Register face for a student")
    public ResponseEntity<MessageResponse> registerFace(
            @ModelAttribute @Valid FaceRegistrationRequest request
    ) {
        faceRecognitionService.registerFace(request.getStudentId(), request.getFaceImage());
        return ResponseEntity.ok(new MessageResponse("Face registered successfully"));
    }

    @PostMapping(value = "/verify", consumes = MediaType.MULTIPART_FORM_DATA_VALUE)
    @PreAuthorize("hasAnyRole('STUDENT', 'ADMIN')")
    @Operation(summary = "Verify face against registered face")
    public ResponseEntity<FaceMatchResult> verifyFace(
            @ModelAttribute @Valid FaceVerificationRequest request
    ) {
        FaceMatchResult result = faceRecognitionService.verifyFace(
            request.getStudentId(), 
            request.getFaceImage()
        );
        return ResponseEntity.ok(result);
    }

    @PostMapping(value = "/update", consumes = MediaType.MULTIPART_FORM_DATA_VALUE)
    @PreAuthorize("hasAnyRole('STUDENT', 'ADMIN')")
    @Operation(summary = "Update registered face")
    public ResponseEntity<MessageResponse> updateFace(
            @RequestParam Long studentId,
            @RequestParam MultipartFile faceImage
    ) {
        faceRecognitionService.updateFace(studentId, faceImage);
        return ResponseEntity.ok(new MessageResponse("Face updated successfully"));
    }

    @DeleteMapping("/{studentId}")
    @PreAuthorize("hasRole('ADMIN')")
    @Operation(summary = "Delete registered face")
    public ResponseEntity<MessageResponse> deleteFace(
            @PathVariable Long studentId
    ) {
        faceRecognitionService.deleteFace(studentId);
        return ResponseEntity.ok(new MessageResponse("Face deleted successfully"));
    }

    @GetMapping("/status/{studentId}")
    @PreAuthorize("hasAnyRole('STUDENT', 'TEACHER', 'ADMIN')")
    @Operation(summary = "Check face registration status")
    public ResponseEntity<Boolean> checkRegistrationStatus(
            @PathVariable Long studentId
    ) {
        boolean isRegistered = faceRecognitionService.isFaceRegistered(studentId);
        return ResponseEntity.ok(isRegistered);
    }
}
