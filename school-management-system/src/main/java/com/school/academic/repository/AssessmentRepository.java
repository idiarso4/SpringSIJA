package com.school.academic.repository;

import com.school.academic.entity.Assessment;
import com.school.masterdata.entity.Student;
import com.school.masterdata.entity.Subject;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.Optional;

@Repository
public interface AssessmentRepository extends JpaRepository<Assessment, Long> {
    
    Page<Assessment> findByStudent(Student student, Pageable pageable);
    
    List<Assessment> findByStudentAndSubjectAndAssessmentType(
        Student student,
        Subject subject,
        Assessment.AssessmentType assessmentType
    );
    
    @Query("SELECT a FROM Assessment a WHERE a.student.id = :studentId " +
           "AND a.subject.id = :subjectId " +
           "AND a.assessmentType = :type " +
           "AND a.attempt = (" +
           "    SELECT MAX(a2.attempt) FROM Assessment a2 " +
           "    WHERE a2.student.id = :studentId " +
           "    AND a2.subject.id = :subjectId " +
           "    AND a2.assessmentType = :type" +
           ")")
    Optional<Assessment> findLatestAssessment(
        Long studentId,
        Long subjectId,
        Assessment.AssessmentType type
    );
    
    @Query("SELECT AVG(a.score) FROM Assessment a " +
           "WHERE a.student.id = :studentId " +
           "AND a.subject.id = :subjectId " +
           "AND a.assessmentType = :type " +
           "AND a.deleted = false")
    Double calculateAverageScore(
        Long studentId,
        Long subjectId,
        Assessment.AssessmentType type
    );
}
