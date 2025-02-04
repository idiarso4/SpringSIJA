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
    
    Optional<Assessment> findFirstByStudentAndSubjectAndAssessmentTypeOrderByDateDesc(
        Student student,
        Subject subject,
        Assessment.AssessmentType assessmentType
    );
    
    @Query("SELECT AVG(a.score) FROM Assessment a " +
           "WHERE a.student = :student " +
           "AND a.subject = :subject " +
           "AND a.assessmentType = :type " +
           "AND a.deleted = false")
    Double calculateAverageScore(
        Student student,
        Subject subject,
        Assessment.AssessmentType type
    );
}
