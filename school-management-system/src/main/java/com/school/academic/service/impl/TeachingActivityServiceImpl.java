package com.school.academic.service.impl;

import com.school.academic.dto.TeachingActivityDTO;
import com.school.academic.entity.TeachingActivity;
import com.school.academic.repository.TeachingActivityRepository;
import com.school.academic.service.TeachingActivityService;
import com.school.common.exception.ResourceNotFoundException;
import com.school.masterdata.entity.ClassRoom;
import com.school.masterdata.entity.Subject;
import com.school.masterdata.entity.Teacher;
import com.school.masterdata.repository.ClassRoomRepository;
import com.school.masterdata.repository.SubjectRepository;
import com.school.masterdata.repository.TeacherRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

import java.time.LocalDate;
import java.util.List;
import java.util.stream.Collectors;

@Service
@RequiredArgsConstructor
public class TeachingActivityServiceImpl implements TeachingActivityService {

    private final TeachingActivityRepository teachingActivityRepository;
    private final TeacherRepository teacherRepository;
    private final ClassRoomRepository classRoomRepository;
    private final SubjectRepository subjectRepository;

    @Override
    public TeachingActivityDTO createTeachingActivity(TeachingActivityDTO dto) {
        Teacher teacher = teacherRepository.findById(dto.getTeacherId())
                .orElseThrow(() -> new ResourceNotFoundException("Teacher not found"));

        ClassRoom classRoom = classRoomRepository.findById(dto.getClassRoomId())
                .orElseThrow(() -> new ResourceNotFoundException("ClassRoom not found"));

        Subject subject = subjectRepository.findById(dto.getSubjectId())
                .orElseThrow(() -> new ResourceNotFoundException("Subject not found"));

        TeachingActivity activity = new TeachingActivity();
        activity.setTeacher(teacher);
        activity.setClassRoom(classRoom);
        activity.setSubject(subject);
        activity.setDate(dto.getDate());
        activity.setStartPeriod(dto.getStartPeriod());
        activity.setEndPeriod(dto.getEndPeriod());
        activity.setStartTime(dto.getStartTime());
        activity.setEndTime(dto.getEndTime());
        activity.setLearningMaterials(dto.getLearningMaterials());

        activity = teachingActivityRepository.save(activity);
        return mapToDTO(activity);
    }

    @Override
    public TeachingActivityDTO getTeachingActivity(Long id) {
        TeachingActivity activity = teachingActivityRepository.findById(id)
                .orElseThrow(() -> new ResourceNotFoundException("Teaching Activity not found"));
        return mapToDTO(activity);
    }

    @Override
    public List<TeachingActivityDTO> getAllTeachingActivities() {
        return teachingActivityRepository.findAll().stream()
                .map(this::mapToDTO)
                .collect(Collectors.toList());
    }

    @Override
    public TeachingActivityDTO updateTeachingActivity(Long id, TeachingActivityDTO dto) {
        TeachingActivity activity = teachingActivityRepository.findById(id)
                .orElseThrow(() -> new ResourceNotFoundException("Teaching Activity not found"));

        Teacher teacher = teacherRepository.findById(dto.getTeacherId())
                .orElseThrow(() -> new ResourceNotFoundException("Teacher not found"));

        ClassRoom classRoom = classRoomRepository.findById(dto.getClassRoomId())
                .orElseThrow(() -> new ResourceNotFoundException("ClassRoom not found"));

        Subject subject = subjectRepository.findById(dto.getSubjectId())
                .orElseThrow(() -> new ResourceNotFoundException("Subject not found"));

        activity.setTeacher(teacher);
        activity.setClassRoom(classRoom);
        activity.setSubject(subject);
        activity.setDate(dto.getDate());
        activity.setStartPeriod(dto.getStartPeriod());
        activity.setEndPeriod(dto.getEndPeriod());
        activity.setStartTime(dto.getStartTime());
        activity.setEndTime(dto.getEndTime());
        activity.setLearningMaterials(dto.getLearningMaterials());

        activity = teachingActivityRepository.save(activity);
        return mapToDTO(activity);
    }

    @Override
    public void deleteTeachingActivity(Long id) {
        TeachingActivity activity = teachingActivityRepository.findById(id)
                .orElseThrow(() -> new ResourceNotFoundException("Teaching Activity not found"));
        teachingActivityRepository.delete(activity);
    }

    private TeachingActivityDTO mapToDTO(TeachingActivity activity) {
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
        return dto;
    }

    @Override
    public Long getMonthlyActivityCount(Long teacherId, int month, int year) {
        return teachingActivityRepository.countByTeacherIdAndMonthAndYear(teacherId, month, year);
    }

    @Override
    public List<TeachingActivityDTO> getTodayActivitiesByTeacher(Long teacherId) {
        LocalDate today = LocalDate.now();
        return teachingActivityRepository.findByTeacherIdAndDate(teacherId, today).stream()
                .map(this::mapToDTO)
                .collect(Collectors.toList());
    }
}
