package com.school.academic.entity;

import com.school.common.entity.BaseEntity;
import com.school.masterdata.entity.ClassRoom;
import com.school.masterdata.entity.Teacher;
import jakarta.persistence.*;
import lombok.Data;
import lombok.EqualsAndHashCode;

import java.time.LocalDateTime;

@Data
@Entity
@Table(name = "teaching_activities")
@EqualsAndHashCode(callSuper = true)
public class TeachingActivity extends BaseEntity {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "teacher_id", nullable = false)
    private Teacher teacher;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "class_room_id", nullable = false)
    private ClassRoom classRoom;

    @Column(nullable = false)
    private LocalDateTime startTime;

    @Column(nullable = false)
    private LocalDateTime endTime;

    @Column(nullable = false)
    private String subject;

    @Column(length = 1000)
    private String description;

    @Column(nullable = false)
    private boolean isCompleted;
}
