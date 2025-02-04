package com.attendance.user.repository;

import com.attendance.user.entity.Student;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.stereotype.Repository;

import java.time.LocalDateTime;
import java.util.List;
import java.util.Optional;

@Repository
public interface StudentRepository extends JpaRepository<Student, Long> {
    Optional<Student> findByStudentNumber(String studentNumber);
    List<Student> findByClassRoomId(Long classId);
    boolean existsByStudentNumber(String studentNumber);

    @Query("SELECT s FROM Student s WHERE s.classRoom.id = ?1 AND s.faceRegistered = true")
    List<Student> findRegisteredStudentsByClassId(Long classId);

    @Query("SELECT s FROM Student s WHERE s.lastAttendance < ?1")
    List<Student> findAbsentStudents(LocalDateTime threshold);

    @Query("SELECT s FROM Student s JOIN s.pklAssignments p WHERE p.company.id = ?1 AND p.status = 'ACTIVE'")
    List<Student> findActiveStudentsByCompanyId(Long companyId);
}
