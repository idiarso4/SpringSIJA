package com.school.academic.service;

import com.school.academic.dto.TeachingActivityDTO;
import com.school.academic.entity.TeachingActivity;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;

import java.time.LocalDate;

public interface TeachingActivityService {
    TeachingActivity createActivity(TeachingActivityDTO activityDTO);
    TeachingActivity updateActivity(Long id, TeachingActivityDTO activityDTO);
    void deleteActivity(Long id);
    TeachingActivity getActivityById(Long id);
    Page<TeachingActivity> getActivitiesByTeacher(Long teacherId, LocalDate startDate, LocalDate endDate, Pageable pageable);
    Page<TeachingActivity> getActivitiesByClassRoom(Long classRoomId, LocalDate startDate, LocalDate endDate, Pageable pageable);
}
