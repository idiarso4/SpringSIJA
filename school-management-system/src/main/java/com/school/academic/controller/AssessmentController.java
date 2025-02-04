package com.school.academic.controller;

import com.school.academic.dto.AssessmentDTO;
import com.school.academic.entity.Assessment;
import com.school.academic.service.AssessmentService;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.http.HttpHeaders;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.*;

import jakarta.validation.Valid;
import java.util.List;

@RestController
@RequestMapping("/api/v1/assessments")
@RequiredArgsConstructor
public class AssessmentController {

    private final AssessmentService assessmentService;

    @PostMapping
    @PreAuthorize("hasRole('TEACHER')")
    public ResponseEntity<AssessmentDTO> createAssessment(
        @Valid @RequestBody AssessmentDTO assessmentDTO
    ) {
        return ResponseEntity.ok(assessmentService.createAssessment(assessmentDTO));
    }

    @PutMapping("/{id}")
    @PreAuthorize("hasRole('TEACHER')")
    public ResponseEntity<AssessmentDTO> updateAssessment(
        @PathVariable Long id,
        @Valid @RequestBody AssessmentDTO assessmentDTO
    ) {
        return ResponseEntity.ok(assessmentService.updateAssessment(id, assessmentDTO));
    }

    @DeleteMapping("/{id}")
    @PreAuthorize("hasRole('TEACHER')")
    public ResponseEntity<Void> deleteAssessment(@PathVariable Long id) {
        assessmentService.deleteAssessment(id);
        return ResponseEntity.ok().build();
    }

    @GetMapping("/{id}")
    @PreAuthorize("hasAnyRole('TEACHER', 'ADMIN')")
    public ResponseEntity<AssessmentDTO> getAssessment(@PathVariable Long id) {
        return ResponseEntity.ok(assessmentService.getAssessmentById(id));
    }

    @GetMapping("/student/{studentId}")
    @PreAuthorize("hasAnyRole('TEACHER', 'ADMIN')")
    public ResponseEntity<Page<AssessmentDTO>> getStudentAssessments(
        @PathVariable Long studentId,
        Pageable pageable
    ) {
        return ResponseEntity.ok(assessmentService.getStudentAssessments(studentId, pageable));
    }

    @GetMapping("/student/{studentId}/subject/{subjectId}")
    @PreAuthorize("hasAnyRole('TEACHER', 'ADMIN')")
    public ResponseEntity<List<AssessmentDTO>> getStudentAssessmentsByType(
        @PathVariable Long studentId,
        @PathVariable Long subjectId,
        @RequestParam Assessment.AssessmentType type
    ) {
        return ResponseEntity.ok(assessmentService.getStudentAssessmentsByType(
            studentId, subjectId, type));
    }

    @GetMapping("/student/{studentId}/subject/{subjectId}/latest")
    @PreAuthorize("hasAnyRole('TEACHER', 'ADMIN')")
    public ResponseEntity<AssessmentDTO> getLatestAssessment(
        @PathVariable Long studentId,
        @PathVariable Long subjectId,
        @RequestParam Assessment.AssessmentType type
    ) {
        AssessmentDTO assessment = assessmentService.getLatestAssessment(
            studentId, subjectId, type);
        return assessment != null ? ResponseEntity.ok(assessment) : ResponseEntity.notFound().build();
    }

    @GetMapping("/student/{studentId}/subject/{subjectId}/average")
    @PreAuthorize("hasAnyRole('TEACHER', 'ADMIN')")
    public ResponseEntity<Double> getAverageScore(
        @PathVariable Long studentId,
        @PathVariable Long subjectId,
        @RequestParam Assessment.AssessmentType type
    ) {
        Double average = assessmentService.calculateAverageScore(studentId, subjectId, type);
        return average != null ? ResponseEntity.ok(average) : ResponseEntity.notFound().build();
    }

    @GetMapping("/report")
    @PreAuthorize("hasAnyRole('TEACHER', 'ADMIN')")
    public ResponseEntity<byte[]> generateReport(
        @RequestParam Long classRoomId,
        @RequestParam Long subjectId
    ) {
        byte[] report = assessmentService.generateAssessmentReport(classRoomId, subjectId);
        
        HttpHeaders headers = new HttpHeaders();
        headers.setContentType(MediaType.APPLICATION_OCTET_STREAM);
        headers.setContentDispositionFormData("attachment", "assessment_report.xlsx");
        
        return ResponseEntity.ok()
            .headers(headers)
            .body(report);
    }
}
