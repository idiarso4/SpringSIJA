package com.school.academic.dto;

import jakarta.validation.constraints.NotNull;

import java.time.LocalDate;
import java.time.LocalTime;

public class TeachingActivityDTO {
    private Long id;

    @NotNull(message = "Teacher ID is required")
    private Long teacherId;

    @NotNull(message = "Class Room ID is required")
    private Long classRoomId;

    @NotNull(message = "Subject ID is required")
    private Long subjectId;

    @NotNull(message = "Date is required")
    private LocalDate date;

    @NotNull(message = "Start Period is required")
    private Integer startPeriod;

    @NotNull(message = "End Period is required")
    private Integer endPeriod;

    @NotNull(message = "Start Time is required")
    private LocalTime startTime;

    @NotNull(message = "End Time is required")
    private LocalTime endTime;

    private String learningMaterials;

    public Long getId() {
        return id;
    }

    public void setId(Long id) {
        this.id = id;
    }

    public Long getTeacherId() {
        return teacherId;
    }

    public void setTeacherId(Long teacherId) {
        this.teacherId = teacherId;
    }

    public Long getClassRoomId() {
        return classRoomId;
    }

    public void setClassRoomId(Long classRoomId) {
        this.classRoomId = classRoomId;
    }

    public Long getSubjectId() {
        return subjectId;
    }

    public void setSubjectId(Long subjectId) {
        this.subjectId = subjectId;
    }

    public LocalDate getDate() {
        return date;
    }

    public void setDate(LocalDate date) {
        this.date = date;
    }

    public Integer getStartPeriod() {
        return startPeriod;
    }

    public void setStartPeriod(Integer startPeriod) {
        this.startPeriod = startPeriod;
    }

    public Integer getEndPeriod() {
        return endPeriod;
    }

    public void setEndPeriod(Integer endPeriod) {
        this.endPeriod = endPeriod;
    }

    public LocalTime getStartTime() {
        return startTime;
    }

    public void setStartTime(LocalTime startTime) {
        this.startTime = startTime;
    }

    public LocalTime getEndTime() {
        return endTime;
    }

    public void setEndTime(LocalTime endTime) {
        this.endTime = endTime;
    }

    public String getLearningMaterials() {
        return learningMaterials;
    }

    public void setLearningMaterials(String learningMaterials) {
        this.learningMaterials = learningMaterials;
    }
}
