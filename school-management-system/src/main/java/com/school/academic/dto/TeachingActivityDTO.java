package com.school.academic.dto;

import jakarta.validation.constraints.NotNull;
import java.time.LocalDate;
import java.time.LocalDateTime;
import java.util.List;

public class TeachingActivityDTO {

    private Long id;

    @NotNull
    private Long teacherId;

    @NotNull
    private Long classRoomId;

    @NotNull
    private Long subjectId;

    @NotNull
    private String topic;

    @NotNull
    private LocalDateTime startTime;

    @NotNull
    private LocalDateTime endTime;

    private String notes;
    
    private LocalDate date;
    
    private Integer startPeriod;
    
    private Integer endPeriod;
    
    private List<String> learningMaterials;

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

    public String getTopic() {
        return topic;
    }

    public void setTopic(String topic) {
        this.topic = topic;
    }

    public LocalDateTime getStartTime() {
        return startTime;
    }

    public void setStartTime(LocalDateTime startTime) {
        this.startTime = startTime;
    }

    public LocalDateTime getEndTime() {
        return endTime;
    }

    public void setEndTime(LocalDateTime endTime) {
        this.endTime = endTime;
    }

    public String getNotes() {
        return notes;
    }

    public void setNotes(String notes) {
        this.notes = notes;
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

    public List<String> getLearningMaterials() {
        return learningMaterials;
    }

    public void setLearningMaterials(List<String> learningMaterials) {
        this.learningMaterials = learningMaterials;
    }
}
