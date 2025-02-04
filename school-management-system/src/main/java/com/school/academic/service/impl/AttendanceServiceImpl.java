package com.school.academic.service.impl;

import com.school.academic.dto.AttendanceDTO;
import com.school.academic.entity.Attendance;
import com.school.academic.entity.TeachingActivity;
import com.school.academic.repository.AttendanceRepository;
import com.school.academic.repository.TeachingActivityRepository;
import com.school.academic.service.AttendanceService;
import com.school.masterdata.entity.Student;
import com.school.masterdata.repository.StudentRepository;
import lombok.RequiredArgsConstructor;
import org.apache.poi.ss.usermodel.*;
import org.apache.poi.xssf.usermodel.XSSFWorkbook;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import jakarta.persistence.EntityNotFoundException;
import java.io.ByteArrayOutputStream;
import java.time.LocalDate;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;

@Service
@RequiredArgsConstructor
@Transactional
public class AttendanceServiceImpl implements AttendanceService {

    private final AttendanceRepository attendanceRepository;
    private final TeachingActivityRepository teachingActivityRepository;
    private final StudentRepository studentRepository;

    @Override
    public List<AttendanceDTO> createBulkAttendance(Long teachingActivityId, List<AttendanceDTO> attendanceDTOs) {
        TeachingActivity activity = teachingActivityRepository.findById(teachingActivityId)
            .orElseThrow(() -> new EntityNotFoundException("Teaching activity not found"));

        List<Attendance> attendances = attendanceDTOs.stream()
            .map(dto -> {
                Student student = studentRepository.findById(dto.getStudentId())
                    .orElseThrow(() -> new EntityNotFoundException("Student not found: " + dto.getStudentId()));
                
                Attendance attendance = new Attendance();
                attendance.setTeachingActivity(activity);
                attendance.setStudent(student);
                attendance.setStatus(dto.getStatus());
                attendance.setNotes(dto.getNotes());
                return attendance;
            })
            .collect(Collectors.toList());

        List<Attendance> savedAttendances = attendanceRepository.saveAll(attendances);
        return savedAttendances.stream()
            .map(this::convertToDTO)
            .collect(Collectors.toList());
    }

    @Override
    public AttendanceDTO updateAttendance(Long id, AttendanceDTO dto) {
        Attendance attendance = attendanceRepository.findById(id)
            .orElseThrow(() -> new EntityNotFoundException("Attendance not found"));
        
        attendance.setStatus(dto.getStatus());
        attendance.setNotes(dto.getNotes());
        
        return convertToDTO(attendanceRepository.save(attendance));
    }

    @Override
    public void deleteAttendance(Long id) {
        Attendance attendance = attendanceRepository.findById(id)
            .orElseThrow(() -> new EntityNotFoundException("Attendance not found"));
        attendance.setDeleted(true);
        attendanceRepository.save(attendance);
    }

    @Override
    @Transactional(readOnly = true)
    public AttendanceDTO getAttendanceById(Long id) {
        return attendanceRepository.findById(id)
            .map(this::convertToDTO)
            .orElseThrow(() -> new EntityNotFoundException("Attendance not found"));
    }

    @Override
    @Transactional(readOnly = true)
    public List<AttendanceDTO> getAttendancesByTeachingActivity(Long teachingActivityId) {
        return attendanceRepository.findByTeachingActivityId(teachingActivityId)
            .stream()
            .map(this::convertToDTO)
            .collect(Collectors.toList());
    }

    @Override
    @Transactional(readOnly = true)
    public Page<AttendanceDTO> getStudentAttendance(
        Long studentId,
        LocalDate startDate,
        LocalDate endDate,
        Pageable pageable
    ) {
        Student student = studentRepository.findById(studentId)
            .orElseThrow(() -> new EntityNotFoundException("Student not found"));
        
        return attendanceRepository.findByStudentAndTeachingActivity_DateBetween(
                student, startDate, endDate, pageable)
            .map(this::convertToDTO);
    }

    @Override
    @Transactional(readOnly = true)
    public Map<String, Long> getDailyAttendanceStats(Long classRoomId, LocalDate date) {
        List<Map<String, Object>> stats = attendanceRepository.getDailyAttendanceStats(classRoomId, date);
        Map<String, Long> result = new HashMap<>();
        
        stats.forEach(stat -> {
            String status = ((Attendance.AttendanceStatus) stat.get("status")).name();
            Long count = ((Number) stat.get("count")).longValue();
            result.put(status, count);
        });
        
        return result;
    }

    @Override
    @Transactional(readOnly = true)
    public Long getMonthlyAbsenceCount(Long studentId, int month, int year) {
        return attendanceRepository.countMonthlyAbsencesByStudent(studentId, month, year);
    }

    @Override
    public byte[] generateAttendanceReport(Long classRoomId, LocalDate startDate, LocalDate endDate) {
        try (Workbook workbook = new XSSFWorkbook()) {
            Sheet sheet = workbook.createSheet("Attendance Report");

            // Create header style
            CellStyle headerStyle = workbook.createCellStyle();
            headerStyle.setFillForegroundColor(IndexedColors.GREY_50_PERCENT.getIndex());
            headerStyle.setFillPattern(FillPatternType.SOLID_FOREGROUND);
            Font headerFont = workbook.createFont();
            headerFont.setBold(true);
            headerFont.setColor(IndexedColors.WHITE.getIndex());
            headerStyle.setFont(headerFont);

            // Create headers
            Row headerRow = sheet.createRow(0);
            String[] headers = {"Date", "Student", "Subject", "Teacher", "Status", "Notes"};
            for (int i = 0; i < headers.length; i++) {
                Cell cell = headerRow.createCell(i);
                cell.setCellValue(headers[i]);
                cell.setCellStyle(headerStyle);
                sheet.autoSizeColumn(i);
            }

            // TODO: Add data rows from repository
            // This is a placeholder for the actual implementation
            
            ByteArrayOutputStream outputStream = new ByteArrayOutputStream();
            workbook.write(outputStream);
            return outputStream.toByteArray();
        } catch (Exception e) {
            throw new RuntimeException("Failed to generate attendance report", e);
        }
    }

    private AttendanceDTO convertToDTO(Attendance attendance) {
        AttendanceDTO dto = new AttendanceDTO();
        dto.setId(attendance.getId());
        dto.setTeachingActivityId(attendance.getTeachingActivity().getId());
        dto.setStudentId(attendance.getStudent().getId());
        dto.setStatus(attendance.getStatus());
        dto.setNotes(attendance.getNotes());

        // Set additional details
        dto.setStudentName(attendance.getStudent().getUser().getFullName());
        dto.setStudentNumber(attendance.getStudent().getStudentNumber());
        dto.setSubjectName(attendance.getTeachingActivity().getSubject().getName());
        dto.setClassName(attendance.getTeachingActivity().getClassRoom().getName());
        dto.setTeacherName(attendance.getTeachingActivity().getTeacher().getUser().getFullName());

        return dto;
    }
}
