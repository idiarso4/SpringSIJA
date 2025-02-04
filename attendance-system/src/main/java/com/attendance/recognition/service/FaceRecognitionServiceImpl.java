package com.attendance.recognition.service;

import com.attendance.recognition.dto.FaceRegistrationRequest;
import com.attendance.recognition.exception.FaceDetectionException;
import com.attendance.recognition.exception.FaceRegistrationException;
import com.attendance.recognition.model.FaceData;
import com.attendance.recognition.model.FaceMatchResult;
import com.attendance.user.entity.Student;
import com.attendance.user.repository.StudentRepository;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.web.multipart.MultipartFile;

import java.io.IOException;
import java.util.Base64;

@Slf4j
@Service
@RequiredArgsConstructor
public class FaceRecognitionServiceImpl implements FaceRecognitionService {

    private final StudentRepository studentRepository;
    private final FaceRecognitionService faceRecognitionCore;
    private static final double MATCH_THRESHOLD = 80.0;

    @Override
    @Transactional
    public void registerFace(Long studentId, MultipartFile faceImage) {
        var student = studentRepository.findById(studentId)
                .orElseThrow(() -> new IllegalArgumentException("Student not found"));

        try {
            // Process and validate face image
            FaceData faceData = faceRecognitionCore.processFaceImage(faceImage);
            
            // Store face data
            student.setFaceEncodingData(Base64.getEncoder().encodeToString(faceData.getFaceBytes()));
            student.setFaceRegistered(true);
            
            studentRepository.save(student);
            
            log.info("Face registered successfully for student: {}", studentId);
        } catch (IOException e) {
            log.error("Failed to process face image for student: {}", studentId, e);
            throw new FaceRegistrationException("Failed to process face image", e);
        } catch (Exception e) {
            log.error("Failed to register face for student: {}", studentId, e);
            throw new FaceRegistrationException("Failed to register face", e);
        }
    }

    @Override
    public FaceMatchResult verifyFace(Long studentId, MultipartFile faceImage) {
        var student = studentRepository.findById(studentId)
                .orElseThrow(() -> new IllegalArgumentException("Student not found"));

        if (!student.getFaceRegistered()) {
            throw new IllegalStateException("Face not registered for student");
        }

        try {
            byte[] storedFaceData = Base64.getDecoder().decode(student.getFaceEncodingData());
            return faceRecognitionCore.matchFace(storedFaceData, faceImage);
        } catch (IOException e) {
            log.error("Failed to process face image for verification: {}", studentId, e);
            throw new FaceDetectionException("Failed to process face image for verification", e);
        }
    }

    @Override
    @Transactional
    public void updateFace(Long studentId, MultipartFile faceImage) {
        var student = studentRepository.findById(studentId)
                .orElseThrow(() -> new IllegalArgumentException("Student not found"));

        try {
            // Process and validate new face image
            FaceData faceData = faceRecognitionCore.processFaceImage(faceImage);
            
            // Update face data
            student.setFaceEncodingData(Base64.getEncoder().encodeToString(faceData.getFaceBytes()));
            studentRepository.save(student);
            
            log.info("Face updated successfully for student: {}", studentId);
        } catch (IOException e) {
            log.error("Failed to update face for student: {}", studentId, e);
            throw new FaceRegistrationException("Failed to update face", e);
        }
    }

    @Override
    @Transactional
    public void deleteFace(Long studentId) {
        var student = studentRepository.findById(studentId)
                .orElseThrow(() -> new IllegalArgumentException("Student not found"));

        student.setFaceEncodingData(null);
        student.setFaceRegistered(false);
        studentRepository.save(student);
        
        log.info("Face data deleted for student: {}", studentId);
    }

    @Override
    public boolean isFaceRegistered(Long studentId) {
        return studentRepository.findById(studentId)
                .map(Student::getFaceRegistered)
                .orElse(false);
    }

    @Override
    public boolean validateLiveness(MultipartFile faceImage) {
        try {
            // Implement liveness detection logic here
            // This could include:
            // 1. Blink detection
            // 2. Head movement detection
            // 3. Facial expression changes
            // 4. Depth analysis
            // For now, return true as a placeholder
            return true;
        } catch (Exception e) {
            log.error("Failed to validate liveness", e);
            return false;
        }
    }
}
