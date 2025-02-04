package com.school.academic.dto;

import com.school.academic.entity.StudentPermission;
import jakarta.validation.constraints.NotNull;
import lombok.Getter;
import lombok.Setter;

import java.time.LocalDate;
import java.time.LocalDateTime;

@Getter
@Setter
public class StudentPermissionDTO {

    private Long id;

    @NotNull
    private Long studentId;

    @NotNull
    private LocalDate startDate;

    @NotNull
    private LocalDate endDate;

    @NotNull
    private String reason;

    private StudentPermission.PermissionStatus status = StudentPermission.PermissionStatus.PENDING;

    private Long dutyTeacherId;

    private String remarks;

    private LocalDateTime processedTime;
}
