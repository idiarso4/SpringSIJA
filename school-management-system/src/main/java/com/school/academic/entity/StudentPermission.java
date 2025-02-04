package com.school.academic.entity;

import com.school.common.entity.BaseEntity;
import com.school.masterdata.entity.Student;
import com.school.masterdata.entity.Teacher;
import jakarta.persistence.*;
import lombok.Getter;
import lombok.Setter;

import java.time.LocalDate;
import java.time.LocalDateTime;

@Entity
@Table(name = "student_permissions")
public class StudentPermission extends BaseEntity {

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "student_id", nullable = false)
    private Student student;

    @Column(name = "start_date", nullable = false)
    private LocalDate startDate;

    @Column(name = "end_date", nullable = false)
    private LocalDate endDate;

    @Column(nullable = false)
    private String reason;

    @Enumerated(EnumType.STRING)
    @Column(nullable = false)
    private PermissionStatus status = PermissionStatus.PENDING;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "duty_teacher_id")
    private Teacher dutyTeacher;

    private String remarks;

    @Column(name = "request_time")
    private LocalDateTime requestTime;

    @Column(name = "processed_time")
    private LocalDateTime processedTime;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "subject_teacher_id")
    private Teacher subjectTeacher;

    public Student getStudent() {
        return student;
    }

    public void setStudent(Student student) {
        this.student = student;
    }

    public Teacher getSubjectTeacher() {
        return subjectTeacher;
    }

    public void setSubjectTeacher(Teacher subjectTeacher) {
        this.subjectTeacher = subjectTeacher;
    }

    public void setStatus(PermissionStatus status) {
        this.status = status;
    }

    public void setRequestTime(LocalDateTime requestTime) {
        this.requestTime = requestTime;
    }

    public void setDutyTeacher(Teacher dutyTeacher) {
        this.dutyTeacher = dutyTeacher;
    }

    public void setRemarks(String remarks) {
        this.remarks = remarks;
    }

    public void setProcessedTime(LocalDateTime processedTime) {
        this.processedTime = processedTime;
    }

    public enum PermissionStatus {
        PENDING,
        APPROVED,
        REJECTED
    }
}
