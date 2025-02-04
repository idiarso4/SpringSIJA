package com.school.academic.dto;

import com.school.academic.entity.AssessmentType;
import jakarta.validation.constraints.NotNull;
import java.time.LocalDate;

public class AssessmentDTO {

    private Long id;

    @NotNull
    private Long studentId;

    @NotNull
    private Long subjectId;

    @NotNull
    private AssessmentType assessmentType;

    @NotNull
    private Double score;

    @NotNull
    private LocalDate date;

    private String notes;

    private Integer attempt = 1;

    public Long getId() {
        return id;
    }

    public void setId(Long id) {
        this.id = id;
    }

    public Long getStudentId() {
        return studentId;
    }

    public void setStudentId(Long studentId) {
        this.studentId = studentId;
    }

    public Long getSubjectId() {
        return subjectId;
    }

    public void setSubjectId(Long subjectId) {
        this.subjectId = subjectId;
    }

    public AssessmentType getAssessmentType() {
        return assessmentType;
    }

    public void setAssessmentType(AssessmentType assessmentType) {
        this.assessmentType = assessmentType;
    }

    public Double getScore() {
        return score;
    }

    public void setScore(Double score) {
        this.score = score;
    }

    public LocalDate getDate() {
        return date;
    }

    public void setDate(LocalDate date) {
        this.date = date;
    }

    public String getNotes() {
        return notes;
    }

    public void setNotes(String notes) {
        this.notes = notes;
    }

    public Integer getAttempt() {
        return attempt;
    }

    public void setAttempt(Integer attempt) {
        this.attempt = attempt;
    }
}
