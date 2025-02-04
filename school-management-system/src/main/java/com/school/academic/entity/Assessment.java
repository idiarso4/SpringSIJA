package com.school.academic.entity;

import com.school.common.entity.BaseEntity;
import com.school.masterdata.entity.Student;
import com.school.masterdata.entity.Subject;
import jakarta.persistence.*;
import lombok.Data;
import lombok.EqualsAndHashCode;

import java.time.LocalDate;

@Data
@Entity
@Table(name = "assessments")
@EqualsAndHashCode(callSuper = true)
public class Assessment extends BaseEntity {

    @ManyToOne
    @JoinColumn(name = "student_id")
    private Student student;

    @ManyToOne
    @JoinColumn(name = "subject_id")
    private Subject subject;

    @Column(name = "date", nullable = false)
    private LocalDate date;

    @Enumerated(EnumType.STRING)
    @Column(name = "assessment_type", nullable = false)
    private AssessmentType assessmentType;

    @Column(name = "attempt", nullable = false)
    private Integer attempt;

    @Column(name = "score", nullable = false)
    private Double score;

    @Column(name = "notes")
    private String notes;

    public enum AssessmentType {
        QUIZ,
        ASSIGNMENT,
        MID_EXAM,
        FINAL_EXAM,
        PRACTICAL_EXAM
    }
}
