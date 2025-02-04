package com.attendance.user.entity;

import com.attendance.common.entity.BaseEntity;
import com.attendance.pkl.entity.PKLAssignment;
import jakarta.persistence.*;
import lombok.Getter;
import lombok.Setter;

import java.util.ArrayList;
import java.util.List;

@Entity
@Table(name = "students")
@Getter
@Setter
public class Student extends BaseEntity {

    @Column(nullable = false, unique = true)
    private String studentNumber;

    @OneToOne
    @JoinColumn(name = "user_id", nullable = false)
    private User user;

    @ManyToOne
    @JoinColumn(name = "class_id")
    private ClassRoom classRoom;

    private String parentName;
    private String parentPhone;
    private Integer entryYear;

    @OneToMany(mappedBy = "student")
    private List<PKLAssignment> pklAssignments = new ArrayList<>();

    @Column(length = 1000)
    private String faceEncodingData;

    private Boolean faceRegistered = false;
    private LocalDateTime lastAttendance;
    private String attendanceStatus;
}
