package com.attendance.pkl.repository;

import com.attendance.pkl.entity.DailyActivity;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.stereotype.Repository;

import java.time.LocalDate;
import java.util.List;
import java.util.Optional;

@Repository
public interface DailyActivityRepository extends JpaRepository<DailyActivity, Long> {
    
    List<DailyActivity> findByPklAssignmentId(Long assignmentId);

    @Query("SELECT d FROM DailyActivity d WHERE d.pklAssignment.id = ?1 AND d.date = ?2")
    Optional<DailyActivity> findByAssignmentAndDate(Long assignmentId, LocalDate date);

    @Query("SELECT d FROM DailyActivity d WHERE d.pklAssignment.student.id = ?1 AND d.date BETWEEN ?2 AND ?3")
    List<DailyActivity> findStudentActivitiesInRange(Long studentId, LocalDate start, LocalDate end);

    @Query("SELECT d FROM DailyActivity d WHERE d.pklAssignment.supervisor.id = ?1 AND d.isApproved = false")
    List<DailyActivity> findPendingApprovalsBySupervisor(Long teacherId);

    @Query("SELECT COUNT(d) FROM DailyActivity d WHERE d.pklAssignment.id = ?1 AND d.isApproved = true")
    long countApprovedActivities(Long assignmentId);

    @Query("""
        SELECT d FROM DailyActivity d 
        WHERE d.pklAssignment.company.id = ?1 
        AND d.date = CURRENT_DATE 
        AND d.isApproved = false
    """)
    List<DailyActivity> findTodayPendingActivitiesByCompany(Long companyId);

    @Query("""
        SELECT AVG(d.rating) FROM DailyActivity d 
        WHERE d.pklAssignment.id = ?1 
        AND d.rating IS NOT NULL
    """)
    Double calculateAverageRating(Long assignmentId);
}
