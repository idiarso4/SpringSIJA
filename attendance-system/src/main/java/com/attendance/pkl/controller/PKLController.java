package com.attendance.pkl.controller;

import com.attendance.pkl.dto.*;
import com.attendance.pkl.service.PKLService;
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
import org.springframework.web.multipart.MultipartFile;

import java.time.LocalDate;
import java.util.List;

@RestController
@RequestMapping("/api/v1/pkl")
@RequiredArgsConstructor
@Tag(name = "PKL Management", description = "PKL (Internship) management APIs")
public class PKLController {

    private final PKLService pklService;

    @PostMapping("/assignments")
    @PreAuthorize("hasRole('ADMIN')")
    @Operation(summary = "Create new PKL assignment")
    public ResponseEntity<PKLAssignmentResponse> createAssignment(
            @RequestBody @Valid CreatePKLAssignmentRequest request
    ) {
        return ResponseEntity.ok(pklService.createAssignment(request));
    }

    @GetMapping("/assignments/student/{studentId}")
    @PreAuthorize("hasAnyRole('STUDENT', 'TEACHER', 'ADMIN')")
    @Operation(summary = "Get student's PKL assignments")
    public ResponseEntity<List<PKLAssignmentResponse>> getStudentAssignments(
            @PathVariable Long studentId
    ) {
        return ResponseEntity.ok(pklService.getStudentAssignments(studentId));
    }

    @GetMapping("/assignments/supervisor/{teacherId}")
    @PreAuthorize("hasAnyRole('TEACHER', 'ADMIN')")
    @Operation(summary = "Get supervisor's PKL assignments")
    public ResponseEntity<List<PKLAssignmentResponse>> getSupervisorAssignments(
            @PathVariable Long teacherId
    ) {
        return ResponseEntity.ok(pklService.getSupervisorAssignments(teacherId));
    }

    @PostMapping("/daily-activities")
    @PreAuthorize("hasRole('STUDENT')")
    @Operation(summary = "Submit daily activity")
    public ResponseEntity<DailyActivityResponse> submitDailyActivity(
            @RequestBody @Valid CreateDailyActivityRequest request
    ) {
        return ResponseEntity.ok(pklService.submitDailyActivity(request));
    }

    @PostMapping(value = "/daily-activities/{activityId}/photos", consumes = MediaType.MULTIPART_FORM_DATA_VALUE)
    @PreAuthorize("hasRole('STUDENT')")
    @Operation(summary = "Upload activity photos")
    public ResponseEntity<MessageResponse> uploadActivityPhotos(
            @PathVariable Long activityId,
            @RequestParam("photos") List<MultipartFile> photos
    ) {
        pklService.uploadActivityPhotos(activityId, photos);
        return ResponseEntity.ok(new MessageResponse("Photos uploaded successfully"));
    }

    @PutMapping("/daily-activities/{activityId}/approve")
    @PreAuthorize("hasRole('TEACHER')")
    @Operation(summary = "Approve daily activity")
    public ResponseEntity<DailyActivityResponse> approveActivity(
            @PathVariable Long activityId,
            @RequestBody @Valid ApproveActivityRequest request
    ) {
        return ResponseEntity.ok(pklService.approveActivity(activityId, request));
    }

    @GetMapping("/daily-activities/student/{studentId}")
    @PreAuthorize("hasAnyRole('STUDENT', 'TEACHER', 'ADMIN')")
    @Operation(summary = "Get student's daily activities")
    public ResponseEntity<List<DailyActivityResponse>> getStudentActivities(
            @PathVariable Long studentId,
            @RequestParam @DateTimeFormat(iso = DateTimeFormat.ISO.DATE) LocalDate startDate,
            @RequestParam @DateTimeFormat(iso = DateTimeFormat.ISO.DATE) LocalDate endDate
    ) {
        return ResponseEntity.ok(pklService.getStudentActivities(studentId, startDate, endDate));
    }

    @GetMapping("/companies")
    @PreAuthorize("hasAnyRole('STUDENT', 'TEACHER', 'ADMIN')")
    @Operation(summary = "Get available companies")
    public ResponseEntity<List<CompanyResponse>> getAvailableCompanies() {
        return ResponseEntity.ok(pklService.getAvailableCompanies());
    }

    @PostMapping("/companies")
    @PreAuthorize("hasRole('ADMIN')")
    @Operation(summary = "Register new company")
    public ResponseEntity<CompanyResponse> registerCompany(
            @RequestBody @Valid CreateCompanyRequest request
    ) {
        return ResponseEntity.ok(pklService.registerCompany(request));
    }

    @GetMapping("/stats/student/{studentId}")
    @PreAuthorize("hasAnyRole('STUDENT', 'TEACHER', 'ADMIN')")
    @Operation(summary = "Get student's PKL statistics")
    public ResponseEntity<PKLStudentStatsResponse> getStudentStats(
            @PathVariable Long studentId
    ) {
        return ResponseEntity.ok(pklService.getStudentPKLStatistics(studentId));
    }

    @GetMapping("/stats/company/{companyId}")
    @PreAuthorize("hasAnyRole('TEACHER', 'ADMIN')")
    @Operation(summary = "Get company's PKL statistics")
    public ResponseEntity<PKLCompanyStatsResponse> getCompanyStats(
            @PathVariable Long companyId
    ) {
        return ResponseEntity.ok(pklService.getCompanyPKLStatistics(companyId));
    }
}
