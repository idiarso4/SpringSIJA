package com.attendance.attendance.entity;

import com.attendance.common.entity.BaseEntity;
import com.attendance.user.entity.Student;
import jakarta.persistence.*;
import lombok.Getter;
import lombok.Setter;

import java.time.LocalDateTime;

@Entity
@Table(name = "attendances")
@Getter
@Setter
public class Attendance extends BaseEntity {

    @ManyToOne
    @JoinColumn(name = "student_id", nullable = false)
    private Student student;

    @Column(nullable = false)
    private LocalDateTime checkInTime;

    private LocalDateTime checkOutTime;

    @Column(nullable = false)
    private String status; // PRESENT, LATE, ABSENT, SICK, PERMISSION

    private Double latitude;
    private Double longitude;
    private Double accuracy;
    private String location;
    private String deviceInfo;
    private String ipAddress;

    @Column(length = 1000)
    private String photoUrl;

    private Double faceMatchConfidence;
    private Boolean isValidLocation = false;
    private String notes;

    @PrePersist
    public void prePersist() {
        if (checkInTime == null) {
            checkInTime = LocalDateTime.now();
        }
    }
}
