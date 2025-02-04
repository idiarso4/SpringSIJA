package com.school.academic.dto;

import com.school.academic.entity.Assessment;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Size;
import lombok.Getter;
import lombok.Setter;

import java.time.LocalDate;

@Getter
@Setter
public class AssessmentDTO {
    private Long id;
    
    @NotNull(message = "Student ID is required")
    private Long studentId;
    
    @NotNull(message = "Subject ID is required")
    private Long subjectId;
    
    @NotNull(message = "Assessment type is required")
    private Assessment.AssessmentType assessmentType;
    
    @NotNull(message = "Score is required")
    private Double score;
    
    @NotNull(message = "Date is required")
    private LocalDate date;
    
    @Size(max = 500, message = "Notes must not exceed 500 characters")
    private String notes;
}
