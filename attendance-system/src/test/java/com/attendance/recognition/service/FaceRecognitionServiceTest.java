package com.attendance.recognition.service;

import com.attendance.recognition.exception.FaceDetectionException;
import com.attendance.recognition.model.FaceData;
import com.attendance.recognition.model.FaceMatchResult;
import com.attendance.user.entity.Student;
import com.attendance.user.repository.StudentRepository;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.mock.web.MockMultipartFile;

import java.io.IOException;
import java.util.Optional;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
class FaceRecognitionServiceTest {

    @Mock
    private StudentRepository studentRepository;

    @Mock
    private FaceRecognitionService faceRecognitionCore;

    @InjectMocks
    private FaceRecognitionServiceImpl faceRecognitionService;

    private Student student;
    private MockMultipartFile imageFile;
    private byte[] faceData;

    @BeforeEach
    void setUp() {
        student = new Student();
        student.setId(1L);
        student.setFaceRegistered(true);
        student.setFaceEncodingData("base64EncodedData");

        faceData = "mockFaceData".getBytes();
        imageFile = new MockMultipartFile(
            "face",
            "face.jpg",
            "image/jpeg",
            "mockImageData".getBytes()
        );
    }

    @Test
    void registerFace_Success() throws IOException {
        // Arrange
        when(studentRepository.findById(1L)).thenReturn(Optional.of(student));
        when(faceRecognitionCore.processFaceImage(any()))
            .thenReturn(new FaceData(faceData));

        // Act
        faceRecognitionService.registerFace(1L, imageFile);

        // Assert
        verify(studentRepository).save(student);
        assertTrue(student.getFaceRegistered());
        assertNotNull(student.getFaceEncodingData());
    }

    @Test
    void registerFace_StudentNotFound() {
        // Arrange
        when(studentRepository.findById(1L)).thenReturn(Optional.empty());

        // Act & Assert
        assertThrows(IllegalArgumentException.class,
            () -> faceRecognitionService.registerFace(1L, imageFile));
    }

    @Test
    void registerFace_ProcessingError() throws IOException {
        // Arrange
        when(studentRepository.findById(1L)).thenReturn(Optional.of(student));
        when(faceRecognitionCore.processFaceImage(any()))
            .thenThrow(new IOException("Processing failed"));

        // Act & Assert
        assertThrows(FaceDetectionException.class,
            () -> faceRecognitionService.registerFace(1L, imageFile));
    }

    @Test
    void verifyFace_Success() throws IOException {
        // Arrange
        FaceMatchResult expectedResult = new FaceMatchResult(true, 95.0);
        when(studentRepository.findById(1L)).thenReturn(Optional.of(student));
        when(faceRecognitionCore.matchFace(any(), any())).thenReturn(expectedResult);

        // Act
        FaceMatchResult result = faceRecognitionService.verifyFace(1L, imageFile);

        // Assert
        assertTrue(result.isMatch());
        assertEquals(95.0, result.getConfidence());
    }

    @Test
    void verifyFace_FaceNotRegistered() {
        // Arrange
        student.setFaceRegistered(false);
        when(studentRepository.findById(1L)).thenReturn(Optional.of(student));

        // Act & Assert
        assertThrows(IllegalStateException.class,
            () -> faceRecognitionService.verifyFace(1L, imageFile));
    }

    @Test
    void deleteFace_Success() {
        // Arrange
        when(studentRepository.findById(1L)).thenReturn(Optional.of(student));

        // Act
        faceRecognitionService.deleteFace(1L);

        // Assert
        verify(studentRepository).save(student);
        assertFalse(student.getFaceRegistered());
        assertNull(student.getFaceEncodingData());
    }

    @Test
    void isFaceRegistered_Success() {
        // Arrange
        when(studentRepository.findById(1L)).thenReturn(Optional.of(student));

        // Act
        boolean result = faceRecognitionService.isFaceRegistered(1L);

        // Assert
        assertTrue(result);
    }

    @Test
    void isFaceRegistered_StudentNotFound() {
        // Arrange
        when(studentRepository.findById(1L)).thenReturn(Optional.empty());

        // Act
        boolean result = faceRecognitionService.isFaceRegistered(1L);

        // Assert
        assertFalse(result);
    }
}
