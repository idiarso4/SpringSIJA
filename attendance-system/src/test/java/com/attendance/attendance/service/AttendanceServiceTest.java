package com.attendance.attendance.service;

import com.attendance.attendance.dto.AttendanceResponse;
import com.attendance.attendance.dto.CheckInRequest;
import com.attendance.attendance.entity.Attendance;
import com.attendance.attendance.repository.AttendanceRepository;
import com.attendance.location.model.Location;
import com.attendance.location.service.LocationValidationService;
import com.attendance.recognition.model.FaceMatchResult;
import com.attendance.recognition.service.FaceRecognitionService;
import com.attendance.storage.service.FileStorageService;
import com.attendance.user.entity.Student;
import com.attendance.user.entity.User;
import com.attendance.user.repository.StudentRepository;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.mock.web.MockMultipartFile;

import java.time.LocalDateTime;
import java.util.Optional;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
class AttendanceServiceTest {

    @Mock
    private AttendanceRepository attendanceRepository;

    @Mock
    private StudentRepository studentRepository;

    @Mock
    private FaceRecognitionService faceRecognitionService;

    @Mock
    private LocationValidationService locationValidationService;

    @Mock
    private FileStorageService fileStorageService;

    @InjectMocks
    private AttendanceServiceImpl attendanceService;

    private Student student;
    private CheckInRequest checkInRequest;
    private MockMultipartFile faceImage;

    @BeforeEach
    void setUp() {
        User user = new User();
        user.setId(1L);
        user.setFullName("John Doe");

        student = new Student();
        student.setId(1L);
        student.setUser(user);
        student.setFaceRegistered(true);

        faceImage = new MockMultipartFile(
            "face",
            "face.jpg",
            "image/jpeg",
            "mockImageData".getBytes()
        );

        checkInRequest = CheckInRequest.builder()
            .studentId(1L)
            .faceImage(faceImage)
            .latitude(1.234)
            .longitude(4.567)
            .accuracy(10.0)
            .location("Test Location")
            .deviceInfo("Test Device")
            .ipAddress("127.0.0.1")
            .build();
    }

    @Test
    void recordCheckIn_Success() {
        // Arrange
        when(studentRepository.findById(1L)).thenReturn(Optional.of(student));
        when(attendanceRepository.findTodayAttendance(1L)).thenReturn(Optional.empty());
        when(faceRecognitionService.verifyFace(eq(1L), any()))
            .thenReturn(new FaceMatchResult(true, 95.0));
        when(locationValidationService.isLocationValid(any(), anyDouble(), anyDouble(), anyDouble()))
            .thenReturn(true);
        when(fileStorageService.storeFile(any())).thenReturn("photo-url");
        when(attendanceRepository.save(any())).thenAnswer(i -> i.getArgument(0));

        // Act
        AttendanceResponse response = attendanceService.recordCheckIn(checkInRequest);

        // Assert
        assertNotNull(response);
        assertEquals(student.getUser().getFullName(), response.getStudentName());
        assertEquals("PRESENT", response.getStatus());
        assertTrue(response.getIsValidLocation());
        assertEquals(95.0, response.getFaceMatchConfidence());

        verify(studentRepository).save(student);
        verify(attendanceRepository).save(any(Attendance.class));
    }

    @Test
    void recordCheckIn_AlreadyCheckedIn() {
        // Arrange
        when(studentRepository.findById(1L)).thenReturn(Optional.of(student));
        when(attendanceRepository.findTodayAttendance(1L))
            .thenReturn(Optional.of(new Attendance()));

        // Act & Assert
        assertThrows(IllegalStateException.class,
            () -> attendanceService.recordCheckIn(checkInRequest));
    }

    @Test
    void recordCheckIn_FaceVerificationFailed() {
        // Arrange
        when(studentRepository.findById(1L)).thenReturn(Optional.of(student));
        when(attendanceRepository.findTodayAttendance(1L)).thenReturn(Optional.empty());
        when(faceRecognitionService.verifyFace(eq(1L), any()))
            .thenReturn(new FaceMatchResult(false, 50.0));

        // Act & Assert
        assertThrows(IllegalStateException.class,
            () -> attendanceService.recordCheckIn(checkInRequest));
    }

    @Test
    void recordCheckIn_InvalidLocation() {
        // Arrange
        when(studentRepository.findById(1L)).thenReturn(Optional.of(student));
        when(attendanceRepository.findTodayAttendance(1L)).thenReturn(Optional.empty());
        when(faceRecognitionService.verifyFace(eq(1L), any()))
            .thenReturn(new FaceMatchResult(true, 95.0));
        when(locationValidationService.isLocationValid(any(), anyDouble(), anyDouble(), anyDouble()))
            .thenReturn(false);
        when(fileStorageService.storeFile(any())).thenReturn("photo-url");
        when(attendanceRepository.save(any())).thenAnswer(i -> i.getArgument(0));

        // Act
        AttendanceResponse response = attendanceService.recordCheckIn(checkInRequest);

        // Assert
        assertNotNull(response);
        assertFalse(response.getIsValidLocation());
    }

    @Test
    void recordCheckIn_Late() {
        // Arrange
        LocalDateTime lateTime = LocalDateTime.now().withHour(9);
        when(studentRepository.findById(1L)).thenReturn(Optional.of(student));
        when(attendanceRepository.findTodayAttendance(1L)).thenReturn(Optional.empty());
        when(faceRecognitionService.verifyFace(eq(1L), any()))
            .thenReturn(new FaceMatchResult(true, 95.0));
        when(locationValidationService.isLocationValid(any(), anyDouble(), anyDouble(), anyDouble()))
            .thenReturn(true);
        when(fileStorageService.storeFile(any())).thenReturn("photo-url");
        when(attendanceRepository.save(any())).thenAnswer(i -> {
            Attendance attendance = (Attendance) i.getArgument(0);
            attendance.setCheckInTime(lateTime);
            return attendance;
        });

        // Act
        AttendanceResponse response = attendanceService.recordCheckIn(checkInRequest);

        // Assert
        assertNotNull(response);
        assertEquals("LATE", response.getStatus());
    }
}
