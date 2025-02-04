package com.school.masterdata.entity;

import com.school.common.entity.BaseEntity;
import jakarta.persistence.*;
import lombok.Getter;
import lombok.Setter;

@Entity
@Table(name = "roles")
@Getter
@Setter
public class RoleEntity extends BaseEntity {

    @Column(name = "name", nullable = false, unique = true)
    private String name;

    @Column(name = "description")
    private String description;
}

public enum UserRole {
    ADMIN,
    TEACHER,
    STUDENT,
    PARENT
}
