package com.school.academic.entity;

import com.school.common.entity.BaseEntity;
import com.school.masterdata.entity.Student;
import com.school.masterdata.entity.Subject;
import jakarta.persistence.*;
import lombok.Getter;
import lombok.Setter;

import java.time.LocalDate;

@Entity
@Table(name = "assessments")
@Getter
@Setter
public class Assessment extends BaseEntity {

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "student_id", nullable = false)
    private Student student;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "subject_id", nullable = false)
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

    @Column(name = "notes", length = 500)
    private String notes;

    public enum AssessmentType {
        DAILY_TEST,
        MID_TERM,
        FINAL_TERM,
        PRACTICAL_TEST
    }
}
