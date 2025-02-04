package com.attendance.user.repository;

import com.attendance.user.entity.Teacher;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.Optional;

@Repository
public interface TeacherRepository extends JpaRepository<Teacher, Long> {
    Optional<Teacher> findByEmployeeNumber(String employeeNumber);
    List<Teacher> findBySpecialization(String specialization);

    @Query("SELECT t FROM Teacher t JOIN t.classes c WHERE c.id = ?1")
    List<Teacher> findByClassId(Long classId);

    @Query("SELECT t FROM Teacher t JOIN t.supervisedAssignments a WHERE a.company.id = ?1")
    List<Teacher> findSupervisorsByCompanyId(Long companyId);

    @Query("SELECT COUNT(a) FROM Teacher t JOIN t.supervisedAssignments a WHERE t.id = ?1 AND a.status = 'ACTIVE'")
    int countActiveAssignments(Long teacherId);
}
