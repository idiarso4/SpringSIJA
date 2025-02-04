package com.school.academic.dto;

import com.school.academic.entity.Attendance;
import jakarta.validation.constraints.NotNull;
import lombok.Data;

import java.time.LocalDateTime;

@Data
public class AttendanceDTO {

    private Long id;

    @NotNull(message = "Status is required")
    private Attendance.AttendanceStatus status;

    @NotNull(message = "Student ID is required")
    private Long studentId;

    @NotNull(message = "Teaching activity ID is required")
    private Long teachingActivityId;

    @NotNull(message = "Check-in time is required")
    private LocalDateTime checkInTime;

    private String notes;
}
