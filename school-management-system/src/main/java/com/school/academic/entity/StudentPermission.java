package com.school.academic.entity;

import com.school.common.entity.BaseEntity;
import com.school.masterdata.entity.Student;
import com.school.masterdata.entity.Teacher;
import jakarta.persistence.*;
import lombok.Getter;
import lombok.Setter;

import java.time.LocalDateTime;

@Entity
@Table(name = "student_permissions")
@Getter
@Setter
public class StudentPermission extends BaseEntity {

    @ManyToOne
    @JoinColumn(name = "student_id", nullable = false)
    private Student student;

    @Column(nullable = false)
    private LocalDateTime requestTime;

    @Column(nullable = false)
    @Enumerated(EnumType.STRING)
    private PermissionStatus status = PermissionStatus.PENDING;

    @ManyToOne
    @JoinColumn(name = "duty_teacher_id")
    private Teacher dutyTeacher;

    @Column(length = 500)
    private String remarks;

    private LocalDateTime processedTime;

    public enum PermissionStatus {
        PENDING,
        APPROVED,
        REJECTED
    }
}
