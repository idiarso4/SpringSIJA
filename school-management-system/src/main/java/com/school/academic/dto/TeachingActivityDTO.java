package com.school.academic.dto;

import lombok.Data;

import jakarta.validation.constraints.*;
import java.time.LocalDate;
import java.time.LocalTime;

@Data
public class TeachingActivityDTO {
    private Long id;
    
    @NotNull(message = "Teacher ID is required")
    private Long teacherId;
    
    @NotNull(message = "Class Room ID is required")
    private Long classRoomId;
    
    @NotNull(message = "Subject ID is required")
    private Long subjectId;
    
    @NotBlank(message = "Topic is required")
    private String topic;
    
    @NotBlank(message = "Description is required")
    private String description;
    
    @NotNull(message = "Date is required")
    private LocalDate date;
    
    @NotNull(message = "Start period is required")
    @Min(value = 1, message = "Start period must be at least 1")
    private Integer startPeriod;
    
    @NotNull(message = "End period is required")
    @Min(value = 1, message = "End period must be at least 1")
    private Integer endPeriod;
    
    @NotNull(message = "Start time is required")
    private LocalTime startTime;
    
    @NotNull(message = "End time is required")
    private LocalTime endTime;
    
    @Size(max = 500, message = "Learning materials must not exceed 500 characters")
    private String learningMaterials;
    
    @Size(max = 50, message = "Teaching media must not exceed 50 characters")
    private String teachingMedia;
    
    @Size(max = 500, message = "Notes must not exceed 500 characters")
    private String notes;
    
    // Teacher details
    private String teacherName;
    private String teacherNumber;
    
    // Class details
    private String className;
    private Integer grade;
    
    // Subject details
    private String subjectName;
    private String subjectCode;
}
