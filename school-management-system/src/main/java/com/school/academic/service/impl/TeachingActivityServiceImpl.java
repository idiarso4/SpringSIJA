package com.school.academic.service.impl;

import com.school.academic.dto.TeachingActivityDTO;
import com.school.academic.entity.TeachingActivity;
import com.school.academic.repository.TeachingActivityRepository;
import com.school.academic.service.TeachingActivityService;
import com.school.masterdata.entity.ClassRoom;
import com.school.masterdata.entity.Subject;
import com.school.masterdata.entity.Teacher;
import com.school.masterdata.repository.ClassRoomRepository;
import com.school.masterdata.repository.SubjectRepository;
import com.school.masterdata.repository.TeacherRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import jakarta.persistence.EntityNotFoundException;
import java.time.LocalDate;
import java.util.List;
import java.util.stream.Collectors;

@Service
@RequiredArgsConstructor
@Transactional
public class TeachingActivityServiceImpl implements TeachingActivityService {

    private final TeachingActivityRepository activityRepository;
    private final TeacherRepository teacherRepository;
    private final ClassRoomRepository classRoomRepository;
    private final SubjectRepository subjectRepository;

    @Override
    public TeachingActivityDTO createActivity(TeachingActivityDTO dto) {
        TeachingActivity activity = new TeachingActivity();
        return saveActivity(activity, dto);
    }

    @Override
    public TeachingActivityDTO updateActivity(Long id, TeachingActivityDTO dto) {
        TeachingActivity activity = activityRepository.findById(id)
            .orElseThrow(() -> new EntityNotFoundException("Teaching activity not found"));
        return saveActivity(activity, dto);
    }

    @Override
    public void deleteActivity(Long id) {
        TeachingActivity activity = activityRepository.findById(id)
            .orElseThrow(() -> new EntityNotFoundException("Teaching activity not found"));
        activity.setDeleted(true);
        activityRepository.save(activity);
    }

    @Override
    @Transactional(readOnly = true)
    public TeachingActivityDTO getActivityById(Long id) {
        return activityRepository.findById(id)
            .map(this::convertToDTO)
            .orElseThrow(() -> new EntityNotFoundException("Teaching activity not found"));
    }

    @Override
    @Transactional(readOnly = true)
    public Page<TeachingActivityDTO> getActivitiesByTeacher(
        Long teacherId,
        LocalDate startDate,
        LocalDate endDate,
        Pageable pageable
    ) {
        Teacher teacher = teacherRepository.findById(teacherId)
            .orElseThrow(() -> new EntityNotFoundException("Teacher not found"));
        return activityRepository.findByTeacherAndDateBetween(teacher, startDate, endDate, pageable)
            .map(this::convertToDTO);
    }

    @Override
    @Transactional(readOnly = true)
    public Page<TeachingActivityDTO> getActivitiesByClassRoom(
        Long classRoomId,
        LocalDate startDate,
        LocalDate endDate,
        Pageable pageable
    ) {
        ClassRoom classRoom = classRoomRepository.findById(classRoomId)
            .orElseThrow(() -> new EntityNotFoundException("Class room not found"));
        return activityRepository.findByClassRoomAndDateBetween(classRoom, startDate, endDate, pageable)
            .map(this::convertToDTO);
    }

    @Override
    @Transactional(readOnly = true)
    public List<TeachingActivityDTO> getTodayActivitiesByTeacher(Long teacherId) {
        return activityRepository.findTodayActivitiesByTeacher(teacherId)
            .stream()
            .map(this::convertToDTO)
            .collect(Collectors.toList());
    }

    @Override
    @Transactional(readOnly = true)
    public Long getMonthlyActivityCount(Long teacherId, int month, int year) {
        return activityRepository.countMonthlyActivitiesByTeacher(teacherId, month, year);
    }

    private TeachingActivityDTO saveActivity(TeachingActivity activity, TeachingActivityDTO dto) {
        Teacher teacher = teacherRepository.findById(dto.getTeacherId())
            .orElseThrow(() -> new EntityNotFoundException("Teacher not found"));
        ClassRoom classRoom = classRoomRepository.findById(dto.getClassRoomId())
            .orElseThrow(() -> new EntityNotFoundException("Class room not found"));
        Subject subject = subjectRepository.findById(dto.getSubjectId())
            .orElseThrow(() -> new EntityNotFoundException("Subject not found"));

        activity.setTeacher(teacher);
        activity.setClassRoom(classRoom);
        activity.setSubject(subject);
        activity.setDate(dto.getDate());
        activity.setStartPeriod(dto.getStartPeriod());
        activity.setEndPeriod(dto.getEndPeriod());
        activity.setStartTime(dto.getStartTime());
        activity.setEndTime(dto.getEndTime());
        activity.setLearningMaterials(dto.getLearningMaterials());
        activity.setTeachingMedia(dto.getTeachingMedia());
        activity.setNotes(dto.getNotes());

        return convertToDTO(activityRepository.save(activity));
    }

    private TeachingActivityDTO convertToDTO(TeachingActivity activity) {
        TeachingActivityDTO dto = new TeachingActivityDTO();
        dto.setId(activity.getId());
        dto.setTeacherId(activity.getTeacher().getId());
        dto.setClassRoomId(activity.getClassRoom().getId());
        dto.setSubjectId(activity.getSubject().getId());
        dto.setDate(activity.getDate());
        dto.setStartPeriod(activity.getStartPeriod());
        dto.setEndPeriod(activity.getEndPeriod());
        dto.setStartTime(activity.getStartTime());
        dto.setEndTime(activity.getEndTime());
        dto.setLearningMaterials(activity.getLearningMaterials());
        dto.setTeachingMedia(activity.getTeachingMedia());
        dto.setNotes(activity.getNotes());

        // Set additional details
        dto.setTeacherName(activity.getTeacher().getUser().getFullName());
        dto.setTeacherNumber(activity.getTeacher().getEmployeeNumber());
        dto.setClassName(activity.getClassRoom().getName());
        dto.setGrade(activity.getClassRoom().getGrade());
        dto.setSubjectName(activity.getSubject().getName());
        dto.setSubjectCode(activity.getSubject().getCode());

        return dto;
    }
}
