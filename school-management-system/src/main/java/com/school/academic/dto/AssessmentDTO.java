package com.school.academic.dto;

import com.school.academic.entity.Assessment;
import lombok.Data;

import jakarta.validation.constraints.*;
import java.time.LocalDate;

@Data
public class AssessmentDTO {
    private Long id;
    
    @NotNull(message = "Student ID is required")
    private Long studentId;
    
    @NotNull(message = "Subject ID is required")
    private Long subjectId;
    
    @NotNull(message = "Date is required")
    private LocalDate date;
    
    @NotNull(message = "Assessment type is required")
    private Assessment.AssessmentType assessmentType;
    
    @NotNull(message = "Attempt number is required")
    @Positive(message = "Attempt number must be positive")
    private Integer attempt;
    
    @NotNull(message = "Score is required")
    @Positive(message = "Score must be positive")
    private Double score;
    
    private String notes;
    
    // Student details
    private String studentName;
    private String studentNumber;
    private String className;
    
    // Subject details
    private String subjectName;
    private String subjectCode;
    
    // Additional fields for reporting
    private Double averageScore;
    private Integer totalAttempts;
}
