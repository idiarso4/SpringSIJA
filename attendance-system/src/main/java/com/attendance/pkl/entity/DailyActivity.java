package com.attendance.pkl.entity;

import com.attendance.common.entity.BaseEntity;
import jakarta.persistence.*;
import lombok.Getter;
import lombok.Setter;

import java.time.LocalDate;
import java.util.ArrayList;
import java.util.List;

@Entity
@Table(name = "daily_activities")
@Getter
@Setter
public class DailyActivity extends BaseEntity {

    @ManyToOne
    @JoinColumn(name = "pkl_assignment_id", nullable = false)
    private PKLAssignment pklAssignment;

    @Column(nullable = false)
    private LocalDate date;

    @Column(nullable = false, length = 1000)
    private String description;

    private String learningOutcome;
    private String challenges;
    private String solutions;

    @ElementCollection
    @CollectionTable(
        name = "daily_activity_photos",
        joinColumns = @JoinColumn(name = "daily_activity_id")
    )
    @Column(name = "photo_url", length = 1000)
    private List<String> photoUrls = new ArrayList<>();

    private String supervisorComment;
    private Boolean isApproved = false;
    private LocalDate approvalDate;
    private Integer rating; // 1-5 stars

    @PrePersist
    public void prePersist() {
        if (date == null) {
            date = LocalDate.now();
        }
    }
}
