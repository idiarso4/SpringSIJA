package com.attendance.pkl.repository;

import com.attendance.pkl.entity.PKLAssignment;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.stereotype.Repository;

import java.time.LocalDate;
import java.util.List;
import java.util.Optional;

@Repository
public interface PKLAssignmentRepository extends JpaRepository<PKLAssignment, Long> {
    
    List<PKLAssignment> findByStudentId(Long studentId);
    List<PKLAssignment> findBySupervisorId(Long teacherId);
    List<PKLAssignment> findByCompanyId(Long companyId);

    @Query("SELECT p FROM PKLAssignment p WHERE p.student.id = ?1 AND p.status = 'ACTIVE'")
    Optional<PKLAssignment> findActiveAssignment(Long studentId);

    @Query("SELECT p FROM PKLAssignment p WHERE p.status = ?1 AND p.startDate <= ?2 AND p.endDate >= ?2")
    List<PKLAssignment> findCurrentAssignmentsByStatus(String status, LocalDate date);

    @Query("SELECT p FROM PKLAssignment p WHERE p.supervisor.id = ?1 AND p.status IN ('PENDING', 'ACTIVE')")
    List<PKLAssignment> findPendingAndActiveAssignments(Long teacherId);

    @Query("""
        SELECT p FROM PKLAssignment p 
        WHERE p.company.id = ?1 
        AND p.status = 'ACTIVE' 
        AND (
            SELECT COUNT(d) FROM DailyActivity d 
            WHERE d.pklAssignment = p 
            AND d.date = CURRENT_DATE
        ) = 0
    """)
    List<PKLAssignment> findMissingDailyActivities(Long companyId);

    @Query("SELECT COUNT(p) FROM PKLAssignment p WHERE p.company.id = ?1 AND p.status = 'ACTIVE'")
    long countActiveStudents(Long companyId);
}
