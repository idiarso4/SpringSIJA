package com.school.academic.entity;

import com.school.masterdata.entity.Student;
import com.school.masterdata.entity.TeachingActivity;
import jakarta.persistence.*;
import lombok.Getter;
import lombok.Setter;

@Entity
@Table(name = "attendances")
@Getter
@Setter
public class Attendance {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "teaching_activity_id", nullable = false)
    private TeachingActivity teachingActivity;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "student_id", nullable = false)
    private Student student;

    @Column(nullable = false)
    private String status;

    @Column(length = 255)
    private String notes;
}
