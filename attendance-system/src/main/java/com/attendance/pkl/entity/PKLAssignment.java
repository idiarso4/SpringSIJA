package com.attendance.pkl.entity;

import com.attendance.common.entity.BaseEntity;
import com.attendance.user.entity.Student;
import com.attendance.user.entity.Teacher;
import jakarta.persistence.*;
import lombok.Getter;
import lombok.Setter;

import java.time.LocalDate;
import java.util.ArrayList;
import java.util.List;

@Entity
@Table(name = "pkl_assignments")
@Getter
@Setter
public class PKLAssignment extends BaseEntity {

    @ManyToOne
    @JoinColumn(name = "student_id", nullable = false)
    private Student student;

    @ManyToOne
    @JoinColumn(name = "supervisor_id", nullable = false)
    private Teacher supervisor;

    @ManyToOne
    @JoinColumn(name = "company_id", nullable = false)
    private Company company;

    @Column(nullable = false)
    private LocalDate startDate;

    @Column(nullable = false)
    private LocalDate endDate;

    private String status; // PENDING, APPROVED, ACTIVE, COMPLETED, REJECTED

    @OneToMany(mappedBy = "pklAssignment", cascade = CascadeType.ALL)
    private List<DailyActivity> dailyActivities = new ArrayList<>();

    private String supervisorNotes;
    private Integer finalScore;
    private String competencyAchievement;
    private String recommendations;

    @Column(nullable = false)
    private Double allowedRadius; // in meters

    private Double companyLatitude;
    private Double companyLongitude;
}
