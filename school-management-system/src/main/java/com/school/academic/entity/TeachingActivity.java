package com.school.academic.entity;

import com.school.masterdata.entity.ClassRoom;
import com.school.masterdata.entity.Subject;
import com.school.masterdata.entity.Teacher;
import jakarta.persistence.*;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

import java.time.LocalDate;
import java.time.LocalTime;

@Entity
@Table(name = "teaching_activities")
@Getter
@Setter
@NoArgsConstructor
public class TeachingActivity {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "teacher_id", nullable = false)
    private Teacher teacher;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "class_room_id", nullable = false)
    private ClassRoom classRoom;

    @ManyToOne(fetch = FetchType.LAZY)
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

    private boolean deleted = false;

    public Long getId() {
        return id;
    }

    public Teacher getTeacher() {
        return teacher;
    }

    public ClassRoom getClassRoom() {
        return classRoom;
    }

    public Subject getSubject() {
        return subject;
    }

    public LocalDate getDate() {
        return date;
    }

    public Integer getStartPeriod() {
        return startPeriod;
    }

    public Integer getEndPeriod() {
        return endPeriod;
    }

    public LocalTime getStartTime() {
        return startTime;
    }

    public LocalTime getEndTime() {
        return endTime;
    }

    public String getLearningMaterials() {
        return learningMaterials;
    }

    public String getTeachingMedia() {
        return teachingMedia;
    }

    public String getNotes() {
        return notes;
    }

    public void setTeacher(Teacher teacher) {
        this.teacher = teacher;
    }

    public void setClassRoom(ClassRoom classRoom) {
        this.classRoom = classRoom;
    }

    public void setSubject(Subject subject) {
        this.subject = subject;
    }

    public void setDate(LocalDate date) {
        this.date = date;
    }

    public void setStartPeriod(Integer startPeriod) {
        this.startPeriod = startPeriod;
    }

    public void setEndPeriod(Integer endPeriod) {
        this.endPeriod = endPeriod;
    }

    public void setStartTime(LocalTime startTime) {
        this.startTime = startTime;
    }

    public void setEndTime(LocalTime endTime) {
        this.endTime = endTime;
    }

    public void setLearningMaterials(String learningMaterials) {
        this.learningMaterials = learningMaterials;
    }

    public void setTeachingMedia(String teachingMedia) {
        this.teachingMedia = teachingMedia;
    }

    public void setNotes(String notes) {
        this.notes = notes;
    }

    public void setDeleted(boolean deleted) {
        this.deleted = deleted;
    }

    public boolean isDeleted() {
        return deleted;
    }
}
