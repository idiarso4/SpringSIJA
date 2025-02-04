package com.school.academic.service.impl;

import com.school.academic.dto.AttendanceDTO;
import com.school.academic.entity.Attendance;
import com.school.academic.entity.AttendanceStatus;
import com.school.academic.entity.TeachingActivity;
import com.school.academic.repository.AttendanceRepository;
import com.school.academic.repository.TeachingActivityRepository;
import com.school.academic.service.AttendanceService;
import com.school.masterdata.entity.Student;
import com.school.masterdata.repository.StudentRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.LocalDate;
import java.util.ArrayList;
import java.util.List;

@Service
@RequiredArgsConstructor
public class AttendanceServiceImpl implements AttendanceService {
    private final AttendanceRepository attendanceRepository;
    private final TeachingActivityRepository teachingActivityRepository;
    private final StudentRepository studentRepository;

    @Override
    @Transactional
    public List<Attendance> createBulkAttendance(Long activityId, List<AttendanceDTO> attendanceDTOs) {
        TeachingActivity activity = teachingActivityRepository.findById(activityId)
                .orElseThrow(() -> new IllegalArgumentException("Teaching activity not found"));

        List<Attendance> attendances = new ArrayList<>();
        for (AttendanceDTO dto : attendanceDTOs) {
            Student student = studentRepository.findById(dto.getStudentId())
                    .orElseThrow(() -> new IllegalArgumentException("Student not found"));

            Attendance attendance = new Attendance();
            attendance.setTeachingActivity(activity);
            attendance.setStudent(student);
            attendance.setStatus(AttendanceStatus.valueOf(dto.getStatus()));
            attendance.setNotes(dto.getNotes());

            attendances.add(attendance);
        }

        return attendanceRepository.saveAll(attendances);
    }

    @Override
    public Attendance getAttendanceById(Long id) {
        return attendanceRepository.findById(id)
                .orElseThrow(() -> new IllegalArgumentException("Attendance not found"));
    }

    @Override
    public List<Attendance> getAttendancesByTeachingActivity(Long activityId) {
        return attendanceRepository.findByTeachingActivityId(activityId);
    }

    @Override
    public Page<Attendance> getStudentAttendance(Long studentId, LocalDate startDate, LocalDate endDate, Pageable pageable) {
        return attendanceRepository.findByStudentIdAndTeachingActivityStartTimeBetween(
                studentId,
                startDate.atStartOfDay(),
                endDate.plusDays(1).atStartOfDay(),
                pageable
        );
    }

    @Override
    public Long getMonthlyAbsenceCount(Long studentId, int month, int year) {
        return attendanceRepository.countByStudentIdAndStatusAndMonthAndYear(
                studentId,
                AttendanceStatus.ABSENT,
                month,
                year
        );
    }
}
