package com.attendance.attendance.service;

import com.attendance.attendance.dto.*;
import com.attendance.attendance.entity.Attendance;
import com.attendance.attendance.repository.AttendanceRepository;
import com.attendance.location.model.Location;
import com.attendance.location.service.LocationValidationService;
import com.attendance.recognition.model.FaceMatchResult;
import com.attendance.recognition.service.FaceRecognitionService;
import com.attendance.user.entity.Student;
import com.attendance.user.repository.StudentRepository;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.LocalDate;
import java.time.LocalDateTime;
import java.time.LocalTime;
import java.util.List;
import java.util.stream.Collectors;

@Slf4j
@Service
@RequiredArgsConstructor
public class AttendanceServiceImpl implements AttendanceService {

    private final AttendanceRepository attendanceRepository;
    private final StudentRepository studentRepository;
    private final FaceRecognitionService faceRecognitionService;
    private final LocationValidationService locationValidationService;
    private final FileStorageService fileStorageService;

    private static final LocalTime LATE_THRESHOLD = LocalTime.of(8, 0); // 8:00 AM
    private static final double ALLOWED_RADIUS = 100.0; // 100 meters

    @Override
    @Transactional
    public AttendanceResponse recordCheckIn(CheckInRequest request) {
        Student student = studentRepository.findById(request.getStudentId())
                .orElseThrow(() -> new IllegalArgumentException("Student not found"));

        // Check if already checked in today
        attendanceRepository.findTodayAttendance(student.getId())
                .ifPresent(a -> {
                    throw new IllegalStateException("Already checked in today");
                });

        // Verify face
        FaceMatchResult faceMatch = faceRecognitionService.verifyFace(
            student.getId(), 
            request.getFaceImage()
        );

        if (!faceMatch.isMatch()) {
            throw new IllegalStateException("Face verification failed");
        }

        // Validate location
        Location location = new Location(
            request.getLatitude(),
            request.getLongitude(),
            request.getAccuracy(),
            request.getProvider(),
            System.currentTimeMillis()
        );

        boolean isValidLocation = locationValidationService.isLocationValid(
            location,
            request.getLatitude(),
            request.getLongitude(),
            ALLOWED_RADIUS
        );

        // Save attendance photo
        String photoUrl = fileStorageService.storeFile(request.getFaceImage());

        // Create attendance record
        Attendance attendance = new Attendance();
        attendance.setStudent(student);
        attendance.setCheckInTime(LocalDateTime.now());
        attendance.setStatus(determineStatus(LocalDateTime.now()));
        attendance.setLatitude(request.getLatitude());
        attendance.setLongitude(request.getLongitude());
        attendance.setAccuracy(request.getAccuracy());
        attendance.setLocation(request.getLocation());
        attendance.setDeviceInfo(request.getDeviceInfo());
        attendance.setIpAddress(request.getIpAddress());
        attendance.setPhotoUrl(photoUrl);
        attendance.setFaceMatchConfidence(faceMatch.getConfidence());
        attendance.setIsValidLocation(isValidLocation);

        attendance = attendanceRepository.save(attendance);

        // Update student's last attendance
        student.setLastAttendance(attendance.getCheckInTime());
        student.setAttendanceStatus(attendance.getStatus());
        studentRepository.save(student);

        return mapToAttendanceResponse(attendance);
    }

    @Override
    @Transactional
    public AttendanceResponse recordCheckOut(CheckOutRequest request) {
        Attendance attendance = attendanceRepository.findTodayAttendance(request.getStudentId())
                .orElseThrow(() -> new IllegalStateException("No check-in record found for today"));

        if (attendance.getCheckOutTime() != null) {
            throw new IllegalStateException("Already checked out");
        }

        attendance.setCheckOutTime(LocalDateTime.now());
        attendance = attendanceRepository.save(attendance);

        return mapToAttendanceResponse(attendance);
    }

    @Override
    public List<AttendanceResponse> getStudentAttendance(Long studentId, LocalDate startDate, LocalDate endDate) {
        LocalDateTime start = startDate.atStartOfDay();
        LocalDateTime end = endDate.atTime(23, 59, 59);

        return attendanceRepository.findStudentAttendanceInRange(studentId, start, end)
                .stream()
                .map(this::mapToAttendanceResponse)
                .collect(Collectors.toList());
    }

    @Override
    public List<ClassAttendanceResponse> getClassAttendance(Long classId, LocalDate date) {
        LocalDateTime start = date.atStartOfDay();
        LocalDateTime end = date.atTime(23, 59, 59);

        return attendanceRepository.findByClassAndDateRange(classId, start, end)
                .stream()
                .map(this::mapToClassAttendanceResponse)
                .collect(Collectors.toList());
    }

    @Override
    public AttendanceStatsResponse getClassStatistics(Long classId, LocalDate startDate, LocalDate endDate) {
        LocalDateTime start = startDate.atStartOfDay();
        LocalDateTime end = endDate.atTime(23, 59, 59);

        List<Object[]> stats = attendanceRepository.getAttendanceStatsByClass(classId, start, end);
        return buildAttendanceStats(stats);
    }

    @Override
    public StudentAttendanceStatsResponse getStudentStatistics(Long studentId, LocalDate startDate, LocalDate endDate) {
        LocalDateTime start = startDate.atStartOfDay();
        LocalDateTime end = endDate.atTime(23, 59, 59);

        List<Attendance> attendances = attendanceRepository.findStudentAttendanceInRange(studentId, start, end);
        return buildStudentStats(attendances);
    }

    private String determineStatus(LocalDateTime checkInTime) {
        return checkInTime.toLocalTime().isAfter(LATE_THRESHOLD) ? "LATE" : "PRESENT";
    }

    private AttendanceResponse mapToAttendanceResponse(Attendance attendance) {
        return AttendanceResponse.builder()
                .id(attendance.getId())
                .studentId(attendance.getStudent().getId())
                .studentName(attendance.getStudent().getUser().getFullName())
                .checkInTime(attendance.getCheckInTime())
                .checkOutTime(attendance.getCheckOutTime())
                .status(attendance.getStatus())
                .location(attendance.getLocation())
                .photoUrl(attendance.getPhotoUrl())
                .faceMatchConfidence(attendance.getFaceMatchConfidence())
                .isValidLocation(attendance.getIsValidLocation())
                .build();
    }

    private ClassAttendanceResponse mapToClassAttendanceResponse(Attendance attendance) {
        return ClassAttendanceResponse.builder()
                .studentId(attendance.getStudent().getId())
                .studentNumber(attendance.getStudent().getStudentNumber())
                .studentName(attendance.getStudent().getUser().getFullName())
                .status(attendance.getStatus())
                .checkInTime(attendance.getCheckInTime())
                .build();
    }

    private AttendanceStatsResponse buildAttendanceStats(List<Object[]> stats) {
        // Implementation for building class attendance statistics
        return AttendanceStatsResponse.builder()
                // ... build stats from the data
                .build();
    }

    private StudentAttendanceStatsResponse buildStudentStats(List<Attendance> attendances) {
        // Implementation for building student attendance statistics
        return StudentAttendanceStatsResponse.builder()
                // ... build stats from the data
                .build();
    }
}
