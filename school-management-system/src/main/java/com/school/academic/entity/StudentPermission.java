package com.school.academic.entity;

import com.school.common.entity.BaseEntity;
import com.school.masterdata.entity.Student;
import com.school.masterdata.entity.Subject;
import com.school.masterdata.entity.Teacher;
import jakarta.persistence.*;
import lombok.Data;
import lombok.EqualsAndHashCode;

import java.time.LocalDateTime;

@Entity
@Data
@EqualsAndHashCode(callSuper = true)
@Table(name = "student_permissions")
public class StudentPermission extends BaseEntity {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "student_id", nullable = false)
    private Student student;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "subject_id", nullable = false)
    private Subject subject;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "subject_teacher_id", nullable = false)
    private Teacher subjectTeacher;

    @ManyToOne
    @JoinColumn(name = "duty_teacher_id")
    private Teacher dutyTeacher;

    @Column(nullable = false)
    @Enumerated(EnumType.STRING)
    private PermissionType type;

    @Column(name = "request_time", nullable = false)
    private LocalDateTime requestTime;

    @Column(nullable = false)
    private LocalDateTime startTime;

    @Column(nullable = false)
    private LocalDateTime endTime;

    @Column(name = "reason", nullable = false)
    private String reason;

    @Column(length = 200)
    private String remarks;

    private LocalDateTime processedTime;

    @Column(nullable = false)
    @Enumerated(EnumType.STRING)
    private PermissionStatus status;

    private String rejectionReason;

    private LocalDateTime approvalTime;

    public enum PermissionType {
        ENTRY_PERMIT,    // Izin Masuk
        EXIT_PERMIT      // Izin Keluar
    }

    public enum PermissionStatus {
        PENDING,        // Menunggu persetujuan
        APPROVED,       // Disetujui
        REJECTED,       // Ditolak
        COMPLETED       // Selesai
    }
}
