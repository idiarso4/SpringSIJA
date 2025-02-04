package com.school.masterdata.entity;

import com.school.common.entity.BaseEntity;
import jakarta.persistence.*;
import lombok.Getter;
import lombok.Setter;

@Entity
@Table(name = "class_rooms")
public class ClassRoom extends BaseEntity {

    @Column(name = "class_code", unique = true, nullable = false)
    private String classCode;

    @Column(name = "class_name", nullable = false)
    private String className;

    @Column(name = "grade_level")
    private Integer gradeLevel;

    @Column(name = "academic_year")
    private String academicYear;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "homeroom_teacher_id")
    private Teacher homeroomTeacher;

    @Column(name = "capacity", nullable = false)
    private Integer capacity;

    public String getClassCode() {
        return classCode;
    }

    public void setClassCode(String classCode) {
        this.classCode = classCode;
    }

    public String getClassName() {
        return className;
    }

    public void setClassName(String className) {
        this.className = className;
    }

    public Integer getGradeLevel() {
        return gradeLevel;
    }

    public void setGradeLevel(Integer gradeLevel) {
        this.gradeLevel = gradeLevel;
    }

    public String getAcademicYear() {
        return academicYear;
    }

    public void setAcademicYear(String academicYear) {
        this.academicYear = academicYear;
    }

    public Teacher getHomeroomTeacher() {
        return homeroomTeacher;
    }

    public void setHomeroomTeacher(Teacher homeroomTeacher) {
        this.homeroomTeacher = homeroomTeacher;
    }

    public Integer getCapacity() {
        return capacity;
    }

    public void setCapacity(Integer capacity) {
        this.capacity = capacity;
    }
}
