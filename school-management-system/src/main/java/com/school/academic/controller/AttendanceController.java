package com.school.academic.controller;

import com.school.academic.dto.AttendanceDTO;
import com.school.academic.service.AttendanceService;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.format.annotation.DateTimeFormat;
import org.springframework.http.HttpHeaders;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.*;

import jakarta.validation.Valid;
import java.time.LocalDate;
import java.util.List;
import java.util.Map;

@RestController
@RequestMapping("/api/v1/attendances")
@RequiredArgsConstructor
public class AttendanceController {

    private final AttendanceService attendanceService;

    @PostMapping("/bulk/{teachingActivityId}")
    @PreAuthorize("hasRole('TEACHER')")
    public ResponseEntity<List<AttendanceDTO>> createBulkAttendance(
        @PathVariable Long teachingActivityId,
        @Valid @RequestBody List<AttendanceDTO> attendances
    ) {
        return ResponseEntity.ok(attendanceService.createBulkAttendance(
            teachingActivityId, attendances));
    }

    @PutMapping("/{id}")
    @PreAuthorize("hasRole('TEACHER')")
    public ResponseEntity<AttendanceDTO> updateAttendance(
        @PathVariable Long id,
        @Valid @RequestBody AttendanceDTO attendanceDTO
    ) {
        return ResponseEntity.ok(attendanceService.updateAttendance(id, attendanceDTO));
    }

    @DeleteMapping("/{id}")
    @PreAuthorize("hasRole('TEACHER')")
    public ResponseEntity<Void> deleteAttendance(@PathVariable Long id) {
        attendanceService.deleteAttendance(id);
        return ResponseEntity.ok().build();
    }

    @GetMapping("/{id}")
    @PreAuthorize("hasAnyRole('TEACHER', 'ADMIN')")
    public ResponseEntity<AttendanceDTO> getAttendance(@PathVariable Long id) {
        return ResponseEntity.ok(attendanceService.getAttendanceById(id));
    }

    @GetMapping("/teaching-activity/{teachingActivityId}")
    @PreAuthorize("hasAnyRole('TEACHER', 'ADMIN')")
    public ResponseEntity<List<AttendanceDTO>> getAttendancesByActivity(
        @PathVariable Long teachingActivityId
    ) {
        return ResponseEntity.ok(attendanceService.getAttendancesByTeachingActivity(
            teachingActivityId));
    }

    @GetMapping("/student/{studentId}")
    @PreAuthorize("hasAnyRole('TEACHER', 'ADMIN')")
    public ResponseEntity<Page<AttendanceDTO>> getStudentAttendance(
        @PathVariable Long studentId,
        @RequestParam @DateTimeFormat(iso = DateTimeFormat.ISO.DATE) LocalDate startDate,
        @RequestParam @DateTimeFormat(iso = DateTimeFormat.ISO.DATE) LocalDate endDate,
        Pageable pageable
    ) {
        return ResponseEntity.ok(attendanceService.getStudentAttendance(
            studentId, startDate, endDate, pageable));
    }

    @GetMapping("/stats/daily")
    @PreAuthorize("hasAnyRole('TEACHER', 'ADMIN')")
    public ResponseEntity<Map<String, Long>> getDailyStats(
        @RequestParam Long classRoomId,
        @RequestParam @DateTimeFormat(iso = DateTimeFormat.ISO.DATE) LocalDate date
    ) {
        return ResponseEntity.ok(attendanceService.getDailyAttendanceStats(classRoomId, date));
    }

    @GetMapping("/stats/monthly-absences")
    @PreAuthorize("hasAnyRole('TEACHER', 'ADMIN')")
    public ResponseEntity<Long> getMonthlyAbsences(
        @RequestParam Long studentId,
        @RequestParam int month,
        @RequestParam int year
    ) {
        return ResponseEntity.ok(attendanceService.getMonthlyAbsenceCount(
            studentId, month, year));
    }

    @GetMapping("/report")
    @PreAuthorize("hasAnyRole('TEACHER', 'ADMIN')")
    public ResponseEntity<byte[]> generateReport(
        @RequestParam Long classRoomId,
        @RequestParam @DateTimeFormat(iso = DateTimeFormat.ISO.DATE) LocalDate startDate,
        @RequestParam @DateTimeFormat(iso = DateTimeFormat.ISO.DATE) LocalDate endDate
    ) {
        byte[] report = attendanceService.generateAttendanceReport(classRoomId, startDate, endDate);
        
        HttpHeaders headers = new HttpHeaders();
        headers.setContentType(MediaType.APPLICATION_OCTET_STREAM);
        headers.setContentDispositionFormData("attachment", "attendance_report.xlsx");
        
        return ResponseEntity.ok()
            .headers(headers)
            .body(report);
    }
}
