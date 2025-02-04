package com.school.academic.entity;

import com.school.common.entity.BaseEntity;
import com.school.masterdata.entity.Student;
import com.school.masterdata.entity.TeachingActivity;
import jakarta.persistence.*;
import lombok.Data;
import lombok.EqualsAndHashCode;

import java.time.LocalDateTime;

@Data
@Entity
@Table(name = "attendances")
@EqualsAndHashCode(callSuper = true)
public class Attendance extends BaseEntity {

    @Enumerated(EnumType.STRING)
    @Column(name = "status", nullable = false)
    private AttendanceStatus status;

    @ManyToOne
    @JoinColumn(name = "student_id")
    private Student student;

    @ManyToOne
    @JoinColumn(name = "teaching_activity_id")
    private TeachingActivity teachingActivity;

    @Column(name = "check_in_time")
    private LocalDateTime checkInTime;

    @Column(name = "notes")
    private String notes;

    public enum AttendanceStatus {
        PRESENT,
        ABSENT,
        LATE,
        EXCUSED
    }
}
