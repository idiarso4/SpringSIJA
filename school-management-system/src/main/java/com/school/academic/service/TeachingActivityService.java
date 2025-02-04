package com.school.academic.service;

import com.school.academic.dto.TeachingActivityDTO;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;

import java.time.LocalDate;
import java.util.List;

public interface TeachingActivityService {
    
    TeachingActivityDTO createActivity(TeachingActivityDTO activityDTO);
    
    TeachingActivityDTO updateActivity(Long id, TeachingActivityDTO activityDTO);
    
    void deleteActivity(Long id);
    
    TeachingActivityDTO getActivityById(Long id);
    
    Page<TeachingActivityDTO> getActivitiesByTeacher(
        Long teacherId,
        LocalDate startDate,
        LocalDate endDate,
        Pageable pageable
    );
    
    Page<TeachingActivityDTO> getActivitiesByClassRoom(
        Long classRoomId,
        LocalDate startDate,
        LocalDate endDate,
        Pageable pageable
    );
    
    List<TeachingActivityDTO> getTodayActivitiesByTeacher(Long teacherId);
    
    Long getMonthlyActivityCount(Long teacherId, int month, int year);
}
