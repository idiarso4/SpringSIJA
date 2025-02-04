package com.school.masterdata.entity;

import com.school.common.entity.BaseEntity;
import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.Table;
import lombok.Data;
import lombok.EqualsAndHashCode;

@Data
@Entity
@EqualsAndHashCode(callSuper = true)
@Table(name = "subjects")
public class Subject extends BaseEntity {

    @Column(nullable = false)
    private String name;

    @Column(nullable = false)
    private String code;

    @Column(nullable = false)
    private Integer grade;

    @Column(name = "theory_hours")
    private Integer theoryHours;

    @Column(name = "practice_hours")
    private Integer practiceHours;

    @Column(length = 500)
    private String description;
}
