package com.school.academic.service.impl;

import com.school.academic.dto.AssessmentDTO;
import com.school.academic.entity.Assessment;
import com.school.academic.repository.AssessmentRepository;
import com.school.academic.service.AssessmentService;
import com.school.masterdata.entity.Student;
import com.school.masterdata.entity.Subject;
import com.school.masterdata.repository.StudentRepository;
import com.school.masterdata.repository.SubjectRepository;
import lombok.RequiredArgsConstructor;
import org.apache.poi.ss.usermodel.*;
import org.apache.poi.xssf.usermodel.XSSFWorkbook;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import jakarta.persistence.EntityNotFoundException;
import java.io.ByteArrayOutputStream;
import java.util.List;
import java.util.stream.Collectors;

@Service
@RequiredArgsConstructor
@Transactional
public class AssessmentServiceImpl implements AssessmentService {

    private final AssessmentRepository assessmentRepository;
    private final StudentRepository studentRepository;
    private final SubjectRepository subjectRepository;

    @Override
    public AssessmentDTO createAssessment(AssessmentDTO dto) {
        Assessment assessment = new Assessment();
        return saveAssessment(assessment, dto);
    }

    @Override
    public AssessmentDTO updateAssessment(Long id, AssessmentDTO dto) {
        Assessment assessment = assessmentRepository.findById(id)
            .orElseThrow(() -> new EntityNotFoundException("Assessment not found"));
        return saveAssessment(assessment, dto);
    }

    @Override
    public void deleteAssessment(Long id) {
        Assessment assessment = assessmentRepository.findById(id)
            .orElseThrow(() -> new EntityNotFoundException("Assessment not found"));
        assessment.setDeleted(true);
        assessmentRepository.save(assessment);
    }

    @Override
    @Transactional(readOnly = true)
    public AssessmentDTO getAssessmentById(Long id) {
        return assessmentRepository.findById(id)
            .map(this::convertToDTO)
            .orElseThrow(() -> new EntityNotFoundException("Assessment not found"));
    }

    @Override
    @Transactional(readOnly = true)
    public Page<AssessmentDTO> getStudentAssessments(Long studentId, Pageable pageable) {
        Student student = studentRepository.findById(studentId)
            .orElseThrow(() -> new EntityNotFoundException("Student not found"));
        return assessmentRepository.findByStudent(student, pageable)
            .map(this::convertToDTO);
    }

    @Override
    @Transactional(readOnly = true)
    public List<AssessmentDTO> getStudentAssessmentsByType(
        Long studentId,
        Long subjectId,
        Assessment.AssessmentType type
    ) {
        Student student = studentRepository.findById(studentId)
            .orElseThrow(() -> new EntityNotFoundException("Student not found"));
        Subject subject = subjectRepository.findById(subjectId)
            .orElseThrow(() -> new EntityNotFoundException("Subject not found"));

        return assessmentRepository.findByStudentAndSubjectAndAssessmentType(student, subject, type)
            .stream()
            .map(this::convertToDTO)
            .collect(Collectors.toList());
    }

    @Override
    @Transactional(readOnly = true)
    public AssessmentDTO getLatestAssessment(
        Long studentId,
        Long subjectId,
        Assessment.AssessmentType type
    ) {
        return assessmentRepository.findLatestAssessment(studentId, subjectId, type)
            .map(this::convertToDTO)
            .orElse(null);
    }

    @Override
    @Transactional(readOnly = true)
    public Double calculateAverageScore(
        Long studentId,
        Long subjectId,
        Assessment.AssessmentType type
    ) {
        return assessmentRepository.calculateAverageScore(studentId, subjectId, type);
    }

    @Override
    public byte[] generateAssessmentReport(Long classRoomId, Long subjectId) {
        try (Workbook workbook = new XSSFWorkbook()) {
            Sheet sheet = workbook.createSheet("Assessment Report");

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
            String[] headers = {
                "Student", "Type", "Date", "Attempt", "Score", "Average Score", "Notes"
            };
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
            throw new RuntimeException("Failed to generate assessment report", e);
        }
    }

    private AssessmentDTO saveAssessment(Assessment assessment, AssessmentDTO dto) {
        Student student = studentRepository.findById(dto.getStudentId())
            .orElseThrow(() -> new EntityNotFoundException("Student not found"));
        Subject subject = subjectRepository.findById(dto.getSubjectId())
            .orElseThrow(() -> new EntityNotFoundException("Subject not found"));

        assessment.setStudent(student);
        assessment.setSubject(subject);
        assessment.setDate(dto.getDate());
        assessment.setAssessmentType(dto.getAssessmentType());
        assessment.setAttempt(dto.getAttempt());
        assessment.setScore(dto.getScore());
        assessment.setNotes(dto.getNotes());

        Assessment savedAssessment = assessmentRepository.save(assessment);
        AssessmentDTO resultDto = convertToDTO(savedAssessment);

        // Add additional calculated fields
        resultDto.setAverageScore(calculateAverageScore(
            student.getId(), subject.getId(), dto.getAssessmentType()));
        
        return resultDto;
    }

    private AssessmentDTO convertToDTO(Assessment assessment) {
        AssessmentDTO dto = new AssessmentDTO();
        dto.setId(assessment.getId());
        dto.setStudentId(assessment.getStudent().getId());
        dto.setSubjectId(assessment.getSubject().getId());
        dto.setDate(assessment.getDate());
        dto.setAssessmentType(assessment.getAssessmentType());
        dto.setAttempt(assessment.getAttempt());
        dto.setScore(assessment.getScore());
        dto.setNotes(assessment.getNotes());

        // Set additional details
        dto.setStudentName(assessment.getStudent().getUser().getFullName());
        dto.setStudentNumber(assessment.getStudent().getStudentNumber());
        dto.setClassName(assessment.getStudent().getClassRoom().getName());
        dto.setSubjectName(assessment.getSubject().getName());
        dto.setSubjectCode(assessment.getSubject().getCode());

        return dto;
    }
}
