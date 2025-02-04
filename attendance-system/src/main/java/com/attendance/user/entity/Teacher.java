package com.attendance.user.entity;

import com.attendance.common.entity.BaseEntity;
import com.attendance.pkl.entity.PKLAssignment;
import jakarta.persistence.*;
import lombok.Getter;
import lombok.Setter;

import java.util.ArrayList;
import java.util.List;

@Entity
@Table(name = "teachers")
@Getter
@Setter
public class Teacher extends BaseEntity {

    @Column(nullable = false, unique = true)
    private String employeeNumber;

    @OneToOne
    @JoinColumn(name = "user_id", nullable = false)
    private User user;

    private String qualifications;
    private String specialization;

    @OneToMany(mappedBy = "supervisor")
    private List<PKLAssignment> supervisedAssignments = new ArrayList<>();

    @ManyToMany
    @JoinTable(
        name = "teacher_classes",
        joinColumns = @JoinColumn(name = "teacher_id"),
        inverseJoinColumns = @JoinColumn(name = "class_id")
    )
    private List<ClassRoom> classes = new ArrayList<>();
}
