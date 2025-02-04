package com.school.academic.service.impl;

import com.school.academic.dto.AttendanceDTO;
import com.school.academic.entity.Attendance;
import com.school.academic.entity.AttendanceStatus;
import com.school.academic.entity.TeachingActivity;
import com.school.academic.repository.AttendanceRepository;
import com.school.academic.repository.TeachingActivityRepository;
import com.school.academic.service.AttendanceService;
import com.school.common.exception.ResourceNotFoundException;
import com.school.masterdata.entity.Student;
import com.school.masterdata.repository.StudentRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

import java.time.LocalDate;
import java.util.List;
import java.util.stream.Collectors;

@Service
@RequiredArgsConstructor
public class AttendanceServiceImpl implements AttendanceService {

    private final AttendanceRepository attendanceRepository;
    private final TeachingActivityRepository teachingActivityRepository;
    private final StudentRepository studentRepository;

    @Override
    public AttendanceDTO createAttendance(AttendanceDTO dto) {
        TeachingActivity teachingActivity = teachingActivityRepository.findById(dto.getTeachingActivityId())
                .orElseThrow(() -> new ResourceNotFoundException("Teaching Activity not found"));

        Student student = studentRepository.findById(dto.getStudentId())
                .orElseThrow(() -> new ResourceNotFoundException("Student not found"));

        Attendance attendance = new Attendance();
        attendance.setTeachingActivity(teachingActivity);
        attendance.setStudent(student);
        attendance.setStatus(AttendanceStatus.valueOf(dto.getStatus()));
        attendance.setNotes(dto.getNotes());

        attendance = attendanceRepository.save(attendance);
        return mapToDTO(attendance);
    }

    @Override
    public AttendanceDTO getAttendance(Long id) {
        Attendance attendance = attendanceRepository.findById(id)
                .orElseThrow(() -> new ResourceNotFoundException("Attendance not found"));
        return mapToDTO(attendance);
    }

    @Override
    public List<AttendanceDTO> getAllAttendances() {
        return attendanceRepository.findAll().stream()
                .map(this::mapToDTO)
                .collect(Collectors.toList());
    }

    @Override
    public AttendanceDTO updateAttendance(Long id, AttendanceDTO dto) {
        Attendance attendance = attendanceRepository.findById(id)
                .orElseThrow(() -> new ResourceNotFoundException("Attendance not found"));

        TeachingActivity teachingActivity = teachingActivityRepository.findById(dto.getTeachingActivityId())
                .orElseThrow(() -> new ResourceNotFoundException("Teaching Activity not found"));

        Student student = studentRepository.findById(dto.getStudentId())
                .orElseThrow(() -> new ResourceNotFoundException("Student not found"));

        attendance.setTeachingActivity(teachingActivity);
        attendance.setStudent(student);
        attendance.setStatus(AttendanceStatus.valueOf(dto.getStatus()));
        attendance.setNotes(dto.getNotes());

        attendance = attendanceRepository.save(attendance);
        return mapToDTO(attendance);
    }

    @Override
    public void deleteAttendance(Long id) {
        Attendance attendance = attendanceRepository.findById(id)
                .orElseThrow(() -> new ResourceNotFoundException("Attendance not found"));
        attendanceRepository.delete(attendance);
    }

    private AttendanceDTO mapToDTO(Attendance attendance) {
        AttendanceDTO dto = new AttendanceDTO();
        dto.setId(attendance.getId());
        dto.setTeachingActivityId(attendance.getTeachingActivity().getId());
        dto.setStudentId(attendance.getStudent().getId());
        dto.setStatus(attendance.getStatus().toString());
        dto.setNotes(attendance.getNotes());
        return dto;
    }

    @Override
    public byte[] generateAttendanceReport(Long teachingActivityId, LocalDate startDate, LocalDate endDate) {
        TeachingActivity teachingActivity = teachingActivityRepository.findById(teachingActivityId)
                .orElseThrow(() -> new ResourceNotFoundException("Teaching Activity not found"));

        List<Attendance> attendances = attendanceRepository.findByTeachingActivityAndTeachingActivity_DateBetween(teachingActivity, startDate, endDate);
        
        // TODO: Implement report generation logic
        return new byte[0]; // Placeholder implementation
    }

    @Override
    public Long getMonthlyAbsenceCount(Long studentId, int month, int year) {
        return attendanceRepository.countMonthlyAbsencesByStudent(studentId, AttendanceStatus.ABSENT, month, year);
    }
}
