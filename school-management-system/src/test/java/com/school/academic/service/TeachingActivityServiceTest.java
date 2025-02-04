package com.school.academic.service;

import com.school.academic.dto.TeachingActivityDTO;
import com.school.academic.entity.TeachingActivity;
import com.school.academic.repository.TeachingActivityRepository;
import com.school.academic.service.impl.TeachingActivityServiceImpl;
import com.school.masterdata.entity.ClassRoom;
import com.school.masterdata.entity.Subject;
import com.school.masterdata.entity.Teacher;
import com.school.masterdata.entity.User;
import com.school.masterdata.repository.ClassRoomRepository;
import com.school.masterdata.repository.SubjectRepository;
import com.school.masterdata.repository.TeacherRepository;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageImpl;
import org.springframework.data.domain.Pageable;

import jakarta.persistence.EntityNotFoundException;
import java.time.LocalDate;
import java.time.LocalTime;
import java.util.List;
import java.util.Optional;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
class TeachingActivityServiceTest {

    @Mock
    private TeachingActivityRepository activityRepository;

    @Mock
    private TeacherRepository teacherRepository;

    @Mock
    private ClassRoomRepository classRoomRepository;

    @Mock
    private SubjectRepository subjectRepository;

    @InjectMocks
    private TeachingActivityServiceImpl teachingActivityService;

    private TeachingActivity activity;
    private TeachingActivityDTO activityDTO;
    private Teacher teacher;
    private ClassRoom classRoom;
    private Subject subject;

    @BeforeEach
    void setUp() {
        User user = new User();
        user.setId(1L);
        user.setUsername("teacher1");
        user.setFullName("John Doe");

        teacher = new Teacher();
        teacher.setId(1L);
        teacher.setUser(user);
        teacher.setEmployeeNumber("T001");

        classRoom = new ClassRoom();
        classRoom.setId(1L);
        classRoom.setName("Class A");
        classRoom.setGrade(10);

        subject = new Subject();
        subject.setId(1L);
        subject.setName("Mathematics");
        subject.setCode("MATH");

        activity = new TeachingActivity();
        activity.setId(1L);
        activity.setTeacher(teacher);
        activity.setClassRoom(classRoom);
        activity.setSubject(subject);
        activity.setDate(LocalDate.now());
        activity.setStartTime(LocalTime.of(8, 0));
        activity.setEndTime(LocalTime.of(9, 30));
        activity.setStartPeriod(1);
        activity.setEndPeriod(2);

        activityDTO = new TeachingActivityDTO();
        activityDTO.setTeacherId(1L);
        activityDTO.setClassRoomId(1L);
        activityDTO.setSubjectId(1L);
        activityDTO.setDate(LocalDate.now());
        activityDTO.setStartTime(LocalTime.of(8, 0));
        activityDTO.setEndTime(LocalTime.of(9, 30));
        activityDTO.setStartPeriod(1);
        activityDTO.setEndPeriod(2);
    }

    @Test
    void createActivity_Success() {
        when(teacherRepository.findById(1L)).thenReturn(Optional.of(teacher));
        when(classRoomRepository.findById(1L)).thenReturn(Optional.of(classRoom));
        when(subjectRepository.findById(1L)).thenReturn(Optional.of(subject));
        when(activityRepository.save(any(TeachingActivity.class))).thenReturn(activity);

        TeachingActivityDTO result = teachingActivityService.createActivity(activityDTO);

        assertNotNull(result);
        assertEquals(teacher.getUser().getFullName(), result.getTeacherName());
        assertEquals(classRoom.getName(), result.getClassName());
        assertEquals(subject.getName(), result.getSubjectName());

        verify(activityRepository).save(any(TeachingActivity.class));
    }

    @Test
    void createActivity_TeacherNotFound() {
        when(teacherRepository.findById(1L)).thenReturn(Optional.empty());

        assertThrows(EntityNotFoundException.class, () -> 
            teachingActivityService.createActivity(activityDTO)
        );

        verify(activityRepository, never()).save(any(TeachingActivity.class));
    }

    @Test
    void getActivitiesByTeacher_Success() {
        Page<TeachingActivity> activityPage = new PageImpl<>(List.of(activity));
        when(teacherRepository.findById(1L)).thenReturn(Optional.of(teacher));
        when(activityRepository.findByTeacherAndDateBetween(
            any(), any(), any(), any(Pageable.class))
        ).thenReturn(activityPage);

        Page<TeachingActivityDTO> result = teachingActivityService.getActivitiesByTeacher(
            1L,
            LocalDate.now(),
            LocalDate.now().plusDays(7),
            Pageable.unpaged()
        );

        assertNotNull(result);
        assertFalse(result.getContent().isEmpty());
        assertEquals(1, result.getContent().size());

        verify(activityRepository).findByTeacherAndDateBetween(
            any(), any(), any(), any(Pageable.class)
        );
    }

    @Test
    void deleteActivity_Success() {
        when(activityRepository.findById(1L)).thenReturn(Optional.of(activity));

        teachingActivityService.deleteActivity(1L);

        verify(activityRepository).save(activity);
        assertTrue(activity.isDeleted());
    }
}
