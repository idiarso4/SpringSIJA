package com.attendance.user.entity;

import com.attendance.common.entity.BaseEntity;
import jakarta.persistence.*;
import lombok.Getter;
import lombok.Setter;

import java.util.ArrayList;
import java.util.List;

@Entity
@Table(name = "class_rooms")
@Getter
@Setter
public class ClassRoom extends BaseEntity {

    @Column(nullable = false)
    private String name;

    private String grade;
    private String department;
    private Integer academicYear;
    private String semester;

    @ManyToOne
    @JoinColumn(name = "homeroom_teacher_id")
    private Teacher homeroomTeacher;

    @OneToMany(mappedBy = "classRoom")
    private List<Student> students = new ArrayList<>();

    @ManyToMany(mappedBy = "classes")
    private List<Teacher> teachers = new ArrayList<>();
}
