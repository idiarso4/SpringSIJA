package com.school.academic.service;

import com.school.academic.dto.TeachingActivityDTO;

import java.util.List;

public interface TeachingActivityService {
    TeachingActivityDTO createTeachingActivity(TeachingActivityDTO dto);
    TeachingActivityDTO getTeachingActivity(Long id);
    List<TeachingActivityDTO> getAllTeachingActivities();
    TeachingActivityDTO updateTeachingActivity(Long id, TeachingActivityDTO dto);
    void deleteTeachingActivity(Long id);
    Long getMonthlyActivityCount(Long teacherId, int month, int year);
    List<TeachingActivityDTO> getTodayActivitiesByTeacher(Long teacherId);
}
