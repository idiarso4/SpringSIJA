package com.school.academic.service.impl;

import com.school.academic.dto.AssessmentDTO;
import com.school.academic.entity.Assessment;
import com.school.academic.repository.AssessmentRepository;
import com.school.academic.service.AssessmentService;
import com.school.common.exception.ResourceNotFoundException;
import com.school.masterdata.entity.Student;
import com.school.masterdata.entity.Subject;
import com.school.masterdata.repository.StudentRepository;
import com.school.masterdata.repository.SubjectRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;
import java.util.stream.Collectors;

@Service
@RequiredArgsConstructor
public class AssessmentServiceImpl implements AssessmentService {

    private final AssessmentRepository assessmentRepository;
    private final StudentRepository studentRepository;
    private final SubjectRepository subjectRepository;

    @Override
    @Transactional
    public AssessmentDTO createAssessment(AssessmentDTO dto) {
        Student student = studentRepository.findById(dto.getStudentId())
                .orElseThrow(() -> new ResourceNotFoundException("Student not found"));

        Subject subject = subjectRepository.findById(dto.getSubjectId())
                .orElseThrow(() -> new ResourceNotFoundException("Subject not found"));

        Assessment assessment = new Assessment();
        assessment.setStudent(student);
        assessment.setSubject(subject);
        assessment.setAssessmentType(dto.getAssessmentType());
        assessment.setScore(dto.getScore());
        assessment.setDate(dto.getDate());
        assessment.setNotes(dto.getNotes());

        assessment = assessmentRepository.save(assessment);
        return mapToDTO(assessment);
    }

    @Override
    @Transactional(readOnly = true)
    public AssessmentDTO getAssessmentById(Long id) {
        Assessment assessment = assessmentRepository.findById(id)
                .orElseThrow(() -> new ResourceNotFoundException("Assessment not found"));
        return mapToDTO(assessment);
    }

    @Override
    @Transactional(readOnly = true)
    public Page<AssessmentDTO> getStudentAssessments(Long studentId, Pageable pageable) {
        Student student = studentRepository.findById(studentId)
                .orElseThrow(() -> new ResourceNotFoundException("Student not found"));
        return assessmentRepository.findByStudent(student, pageable)
                .map(this::mapToDTO);
    }

    @Override
    @Transactional
    public AssessmentDTO updateAssessment(Long id, AssessmentDTO dto) {
        Assessment assessment = assessmentRepository.findById(id)
                .orElseThrow(() -> new ResourceNotFoundException("Assessment not found"));

        Student student = studentRepository.findById(dto.getStudentId())
                .orElseThrow(() -> new ResourceNotFoundException("Student not found"));

        Subject subject = subjectRepository.findById(dto.getSubjectId())
                .orElseThrow(() -> new ResourceNotFoundException("Subject not found"));

        assessment.setStudent(student);
        assessment.setSubject(subject);
        assessment.setAssessmentType(dto.getAssessmentType());
        assessment.setScore(dto.getScore());
        assessment.setDate(dto.getDate());
        assessment.setNotes(dto.getNotes());

        assessment = assessmentRepository.save(assessment);
        return mapToDTO(assessment);
    }

    @Override
    @Transactional
    public void deleteAssessment(Long id) {
        Assessment assessment = assessmentRepository.findById(id)
                .orElseThrow(() -> new ResourceNotFoundException("Assessment not found"));
        assessmentRepository.delete(assessment);
    }

    @Override
    public List<AssessmentDTO> getStudentAssessmentsByType(Long studentId, Long subjectId, Assessment.AssessmentType type) {
        Student student = studentRepository.findById(studentId)
                .orElseThrow(() -> new ResourceNotFoundException("Student not found"));
        Subject subject = subjectRepository.findById(subjectId)
                .orElseThrow(() -> new ResourceNotFoundException("Subject not found"));
        return assessmentRepository.findByStudentAndSubjectAndAssessmentType(student, subject, type).stream()
                .map(this::mapToDTO)
                .collect(Collectors.toList());
    }

    @Override
    public AssessmentDTO getLatestAssessment(Long studentId, Long subjectId, Assessment.AssessmentType type) {
        Student student = studentRepository.findById(studentId)
                .orElseThrow(() -> new ResourceNotFoundException("Student not found"));
        Subject subject = subjectRepository.findById(subjectId)
                .orElseThrow(() -> new ResourceNotFoundException("Subject not found"));
        Assessment assessment = assessmentRepository.findFirstByStudentAndSubjectAndAssessmentTypeOrderByDateDesc(student, subject, type)
                .orElseThrow(() -> new ResourceNotFoundException("Assessment not found"));
        return mapToDTO(assessment);
    }

    @Override
    public Double calculateAverageScore(Long studentId, Long subjectId, Assessment.AssessmentType type) {
        Student student = studentRepository.findById(studentId)
                .orElseThrow(() -> new ResourceNotFoundException("Student not found"));
        Subject subject = subjectRepository.findById(subjectId)
                .orElseThrow(() -> new ResourceNotFoundException("Subject not found"));
        return assessmentRepository.calculateAverageScore(student, subject, type);
    }

    @Override
    public byte[] generateAssessmentReport(Long classRoomId, Long subjectId) {
        // TODO: Implement report generation logic
        throw new UnsupportedOperationException("Report generation not implemented yet");
    }

    private AssessmentDTO mapToDTO(Assessment assessment) {
        AssessmentDTO dto = new AssessmentDTO();
        dto.setId(assessment.getId());
        dto.setStudentId(assessment.getStudent().getId());
        dto.setSubjectId(assessment.getSubject().getId());
        dto.setAssessmentType(assessment.getAssessmentType());
        dto.setScore(assessment.getScore());
        dto.setDate(assessment.getDate());
        dto.setNotes(assessment.getNotes());
        return dto;
    }
}
