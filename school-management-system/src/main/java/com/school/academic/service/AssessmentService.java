package com.school.academic.service;

import com.school.academic.dto.AssessmentDTO;
import com.school.academic.entity.Assessment;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;

import java.util.List;

public interface AssessmentService {
    
    AssessmentDTO createAssessment(AssessmentDTO assessmentDTO);
    
    AssessmentDTO updateAssessment(Long id, AssessmentDTO assessmentDTO);
    
    void deleteAssessment(Long id);
    
    AssessmentDTO getAssessmentById(Long id);
    
    Page<AssessmentDTO> getStudentAssessments(Long studentId, Pageable pageable);
    
    List<AssessmentDTO> getStudentAssessmentsByType(
        Long studentId,
        Long subjectId,
        Assessment.AssessmentType type
    );
    
    AssessmentDTO getLatestAssessment(
        Long studentId,
        Long subjectId,
        Assessment.AssessmentType type
    );
    
    Double calculateAverageScore(
        Long studentId,
        Long subjectId,
        Assessment.AssessmentType type
    );
    
    // Additional method for generating assessment reports
    byte[] generateAssessmentReport(Long classRoomId, Long subjectId);
}
