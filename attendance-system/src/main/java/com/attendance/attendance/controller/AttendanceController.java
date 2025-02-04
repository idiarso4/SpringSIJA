package com.attendance.attendance.controller;

import com.attendance.attendance.dto.*;
import com.attendance.attendance.service.AttendanceService;
import com.attendance.security.dto.MessageResponse;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.tags.Tag;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.format.annotation.DateTimeFormat;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.*;

import java.time.LocalDate;
import java.util.List;

@RestController
@RequestMapping("/api/v1/attendance")
@RequiredArgsConstructor
@Tag(name = "Attendance", description = "Attendance management APIs")
public class AttendanceController {

    private final AttendanceService attendanceService;

    @PostMapping(value = "/check-in", consumes = MediaType.MULTIPART_FORM_DATA_VALUE)
    @PreAuthorize("hasRole('STUDENT')")
    @Operation(summary = "Record student check-in")
    public ResponseEntity<AttendanceResponse> checkIn(
            @ModelAttribute @Valid CheckInRequest request
    ) {
        return ResponseEntity.ok(attendanceService.recordCheckIn(request));
    }

    @PostMapping("/check-out")
    @PreAuthorize("hasRole('STUDENT')")
    @Operation(summary = "Record student check-out")
    public ResponseEntity<AttendanceResponse> checkOut(
            @RequestBody @Valid CheckOutRequest request
    ) {
        return ResponseEntity.ok(attendanceService.recordCheckOut(request));
    }

    @GetMapping("/student/{studentId}")
    @PreAuthorize("hasAnyRole('STUDENT', 'TEACHER', 'ADMIN')")
    @Operation(summary = "Get student attendance history")
    public ResponseEntity<List<AttendanceResponse>> getStudentAttendance(
            @PathVariable Long studentId,
            @RequestParam @DateTimeFormat(iso = DateTimeFormat.ISO.DATE) LocalDate startDate,
            @RequestParam @DateTimeFormat(iso = DateTimeFormat.ISO.DATE) LocalDate endDate
    ) {
        return ResponseEntity.ok(attendanceService.getStudentAttendance(studentId, startDate, endDate));
    }

    @GetMapping("/class/{classId}")
    @PreAuthorize("hasAnyRole('TEACHER', 'ADMIN')")
    @Operation(summary = "Get class attendance")
    public ResponseEntity<List<ClassAttendanceResponse>> getClassAttendance(
            @PathVariable Long classId,
            @RequestParam @DateTimeFormat(iso = DateTimeFormat.ISO.DATE) LocalDate date
    ) {
        return ResponseEntity.ok(attendanceService.getClassAttendance(classId, date));
    }

    @GetMapping("/stats/class/{classId}")
    @PreAuthorize("hasAnyRole('TEACHER', 'ADMIN')")
    @Operation(summary = "Get class attendance statistics")
    public ResponseEntity<AttendanceStatsResponse> getClassStats(
            @PathVariable Long classId,
            @RequestParam @DateTimeFormat(iso = DateTimeFormat.ISO.DATE) LocalDate startDate,
            @RequestParam @DateTimeFormat(iso = DateTimeFormat.ISO.DATE) LocalDate endDate
    ) {
        return ResponseEntity.ok(attendanceService.getClassStatistics(classId, startDate, endDate));
    }

    @GetMapping("/stats/student/{studentId}")
    @PreAuthorize("hasAnyRole('STUDENT', 'TEACHER', 'ADMIN')")
    @Operation(summary = "Get student attendance statistics")
    public ResponseEntity<StudentAttendanceStatsResponse> getStudentStats(
            @PathVariable Long studentId,
            @RequestParam @DateTimeFormat(iso = DateTimeFormat.ISO.DATE) LocalDate startDate,
            @RequestParam @DateTimeFormat(iso = DateTimeFormat.ISO.DATE) LocalDate endDate
    ) {
        return ResponseEntity.ok(attendanceService.getStudentStatistics(studentId, startDate, endDate));
    }

    @PutMapping("/{attendanceId}")
    @PreAuthorize("hasRole('ADMIN')")
    @Operation(summary = "Update attendance record")
    public ResponseEntity<AttendanceResponse> updateAttendance(
            @PathVariable Long attendanceId,
            @RequestBody @Valid UpdateAttendanceRequest request
    ) {
        return ResponseEntity.ok(attendanceService.updateAttendance(attendanceId, request));
    }

    @DeleteMapping("/{attendanceId}")
    @PreAuthorize("hasRole('ADMIN')")
    @Operation(summary = "Delete attendance record")
    public ResponseEntity<MessageResponse> deleteAttendance(
            @PathVariable Long attendanceId
    ) {
        attendanceService.deleteAttendance(attendanceId);
        return ResponseEntity.ok(new MessageResponse("Attendance record deleted successfully"));
    }
}
