package com.attendance.pkl.entity;

import com.attendance.common.entity.BaseEntity;
import jakarta.persistence.*;
import lombok.Getter;
import lombok.Setter;

import java.util.ArrayList;
import java.util.List;

@Entity
@Table(name = "companies")
@Getter
@Setter
public class Company extends BaseEntity {

    @Column(nullable = false)
    private String name;

    @Column(nullable = false)
    private String address;

    private String city;
    private String phone;
    private String email;
    private String website;

    @Column(nullable = false)
    private String supervisorName;

    private String supervisorPhone;
    private String supervisorEmail;
    private String businessField;
    private Integer employeeCount;

    @Column(nullable = false)
    private Double latitude;

    @Column(nullable = false)
    private Double longitude;

    private String status; // ACTIVE, INACTIVE, BLACKLISTED

    @OneToMany(mappedBy = "company")
    private List<PKLAssignment> assignments = new ArrayList<>();

    private String notes;
    private String requirements;
    private Integer maxStudents;
    private Boolean isVerified = false;
}
