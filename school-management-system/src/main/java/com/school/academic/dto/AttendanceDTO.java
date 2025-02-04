package com.school.academic.dto;

import com.school.academic.entity.AttendanceStatus;
import jakarta.validation.constraints.NotNull;

public class AttendanceDTO {
    private Long id;
    
    @NotNull
    private Long teachingActivityId;
    
    @NotNull
    private Long studentId;
    
    @NotNull
    private AttendanceStatus status;
    
    private String notes;

    public Long getId() {
        return id;
    }

    public void setId(Long id) {
        this.id = id;
    }

    public Long getTeachingActivityId() {
        return teachingActivityId;
    }

    public void setTeachingActivityId(Long teachingActivityId) {
        this.teachingActivityId = teachingActivityId;
    }

    public Long getStudentId() {
        return studentId;
    }

    public void setStudentId(Long studentId) {
        this.studentId = studentId;
    }

    public AttendanceStatus getStatus() {
        return status;
    }

    public void setStatus(AttendanceStatus status) {
        this.status = status;
    }

    public String getNotes() {
        return notes;
    }

    public void setNotes(String notes) {
        this.notes = notes;
    }
}
