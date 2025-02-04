package com.school.academic.entity;

import com.school.common.entity.BaseEntity;
import com.school.masterdata.entity.Student;
import com.school.masterdata.entity.TeachingActivity;
import jakarta.persistence.*;
import lombok.Getter;
import lombok.Setter;

@Entity
@Table(name = "attendances")
public class Attendance extends BaseEntity {

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "teaching_activity_id", nullable = false)
    private TeachingActivity teachingActivity;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "student_id", nullable = false)
    private Student student;

    @Enumerated(EnumType.STRING)
    @Column(nullable = false)
    private AttendanceStatus status;

    private String notes;

    public TeachingActivity getTeachingActivity() {
        return teachingActivity;
    }

    public void setTeachingActivity(TeachingActivity teachingActivity) {
        this.teachingActivity = teachingActivity;
    }

    public Student getStudent() {
        return student;
    }

    public void setStudent(Student student) {
        this.student = student;
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

    @Override
    public Long getId() {
        return super.getId();
    }
}
