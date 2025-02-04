package com.school.academic.dto;

import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import lombok.Data;

import java.time.LocalDateTime;

@Data
public class TeachingActivityDTO {
    private Long id;

    @NotNull
    private Long teacherId;

    @NotNull
    private Long classRoomId;

    @NotNull
    private LocalDateTime startTime;

    @NotNull
    private LocalDateTime endTime;

    @NotBlank
    private String subject;

    private String description;

    private boolean completed;
}
