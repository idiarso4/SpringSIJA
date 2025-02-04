package com.school.academic.entity;

import com.school.common.entity.BaseEntity;
import com.school.masterdata.entity.ClassRoom;
import com.school.masterdata.entity.Subject;
import com.school.masterdata.entity.Teacher;
import jakarta.persistence.*;
import lombok.Getter;
import lombok.Setter;

import java.time.LocalDate;
import java.time.LocalTime;

@Entity
@Table(name = "teaching_activities")
@Getter
@Setter
public class TeachingActivity extends BaseEntity {
    
    @ManyToOne
    @JoinColumn(name = "teacher_id", nullable = false)
    private Teacher teacher;

    @ManyToOne
    @JoinColumn(name = "class_room_id", nullable = false)
    private ClassRoom classRoom;

    @ManyToOne
    @JoinColumn(name = "subject_id", nullable = false)
    private Subject subject;

    @Column(nullable = false)
    private LocalDate date;

    @Column(name = "start_period", nullable = false)
    private Integer startPeriod;

    @Column(name = "end_period", nullable = false)
    private Integer endPeriod;

    @Column(name = "start_time", nullable = false)
    private LocalTime startTime;

    @Column(name = "end_time", nullable = false)
    private LocalTime endTime;

    @Column(name = "learning_materials", length = 500)
    private String learningMaterials;

    @Column(name = "teaching_media", length = 50)
    private String teachingMedia;

    @Column(name = "notes", length = 500)
    private String notes;
}
