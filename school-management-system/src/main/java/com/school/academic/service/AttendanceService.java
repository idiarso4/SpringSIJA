package com.school.academic.service;

import com.school.academic.dto.AttendanceDTO;

import java.time.LocalDate;
import java.util.List;

public interface AttendanceService {
    AttendanceDTO createAttendance(AttendanceDTO dto);
    AttendanceDTO getAttendance(Long id);
    List<AttendanceDTO> getAllAttendances();
    AttendanceDTO updateAttendance(Long id, AttendanceDTO dto);
    void deleteAttendance(Long id);
    byte[] generateAttendanceReport(Long teachingActivityId, LocalDate startDate, LocalDate endDate);
    Long getMonthlyAbsenceCount(Long studentId, int month, int year);
}
