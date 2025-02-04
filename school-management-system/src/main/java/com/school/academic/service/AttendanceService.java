package com.school.academic.service;

import com.school.academic.dto.AttendanceDTO;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;

import java.time.LocalDate;
import java.util.List;
import java.util.Map;

public interface AttendanceService {
    
    List<AttendanceDTO> createBulkAttendance(Long teachingActivityId, List<AttendanceDTO> attendances);
    
    AttendanceDTO updateAttendance(Long id, AttendanceDTO attendanceDTO);
    
    void deleteAttendance(Long id);
    
    AttendanceDTO getAttendanceById(Long id);
    
    List<AttendanceDTO> getAttendancesByTeachingActivity(Long teachingActivityId);
    
    Page<AttendanceDTO> getStudentAttendance(
        Long studentId,
        LocalDate startDate,
        LocalDate endDate,
        Pageable pageable
    );
    
    Map<String, Long> getDailyAttendanceStats(Long classRoomId, LocalDate date);
    
    Long getMonthlyAbsenceCount(Long studentId, int month, int year);
    
    // Additional method for generating attendance reports
    byte[] generateAttendanceReport(Long classRoomId, LocalDate startDate, LocalDate endDate);
}
