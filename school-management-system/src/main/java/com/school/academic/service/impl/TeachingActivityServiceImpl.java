package com.school.academic.service.impl;

import com.school.academic.dto.TeachingActivityDTO;
import com.school.academic.entity.TeachingActivity;
import com.school.academic.repository.TeachingActivityRepository;
import com.school.academic.service.TeachingActivityService;
import com.school.masterdata.entity.ClassRoom;
import com.school.masterdata.entity.Teacher;
import com.school.masterdata.repository.ClassRoomRepository;
import com.school.masterdata.repository.TeacherRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.LocalDate;

@Service
@RequiredArgsConstructor
public class TeachingActivityServiceImpl implements TeachingActivityService {
    private final TeachingActivityRepository teachingActivityRepository;
    private final TeacherRepository teacherRepository;
    private final ClassRoomRepository classRoomRepository;

    @Override
    @Transactional
    public TeachingActivity createActivity(TeachingActivityDTO activityDTO) {
        Teacher teacher = teacherRepository.findById(activityDTO.getTeacherId())
                .orElseThrow(() -> new IllegalArgumentException("Teacher not found"));

        ClassRoom classRoom = classRoomRepository.findById(activityDTO.getClassRoomId())
                .orElseThrow(() -> new IllegalArgumentException("Class room not found"));

        TeachingActivity activity = new TeachingActivity();
        activity.setTeacher(teacher);
        activity.setClassRoom(classRoom);
        activity.setStartTime(activityDTO.getStartTime());
        activity.setEndTime(activityDTO.getEndTime());
        activity.setSubject(activityDTO.getSubject());
        activity.setDescription(activityDTO.getDescription());
        activity.setCompleted(false);

        return teachingActivityRepository.save(activity);
    }

    @Override
    @Transactional
    public TeachingActivity updateActivity(Long id, TeachingActivityDTO activityDTO) {
        TeachingActivity activity = getActivityById(id);

        if (activityDTO.getTeacherId() != null) {
            Teacher teacher = teacherRepository.findById(activityDTO.getTeacherId())
                    .orElseThrow(() -> new IllegalArgumentException("Teacher not found"));
            activity.setTeacher(teacher);
        }

        if (activityDTO.getClassRoomId() != null) {
            ClassRoom classRoom = classRoomRepository.findById(activityDTO.getClassRoomId())
                    .orElseThrow(() -> new IllegalArgumentException("Class room not found"));
            activity.setClassRoom(classRoom);
        }

        if (activityDTO.getStartTime() != null) {
            activity.setStartTime(activityDTO.getStartTime());
        }

        if (activityDTO.getEndTime() != null) {
            activity.setEndTime(activityDTO.getEndTime());
        }

        if (activityDTO.getSubject() != null) {
            activity.setSubject(activityDTO.getSubject());
        }

        if (activityDTO.getDescription() != null) {
            activity.setDescription(activityDTO.getDescription());
        }

        return teachingActivityRepository.save(activity);
    }

    @Override
    @Transactional
    public void deleteActivity(Long id) {
        if (!teachingActivityRepository.existsById(id)) {
            throw new IllegalArgumentException("Teaching activity not found");
        }
        teachingActivityRepository.deleteById(id);
    }

    @Override
    public TeachingActivity getActivityById(Long id) {
        return teachingActivityRepository.findById(id)
                .orElseThrow(() -> new IllegalArgumentException("Teaching activity not found"));
    }

    @Override
    public Page<TeachingActivity> getActivitiesByTeacher(Long teacherId, LocalDate startDate, LocalDate endDate, Pageable pageable) {
        return teachingActivityRepository.findByTeacherIdAndStartTimeBetween(
                teacherId,
                startDate.atStartOfDay(),
                endDate.plusDays(1).atStartOfDay(),
                pageable
        );
    }

    @Override
    public Page<TeachingActivity> getActivitiesByClassRoom(Long classRoomId, LocalDate startDate, LocalDate endDate, Pageable pageable) {
        return teachingActivityRepository.findByClassRoomIdAndStartTimeBetween(
                classRoomId,
                startDate.atStartOfDay(),
                endDate.plusDays(1).atStartOfDay(),
                pageable
        );
    }
}
