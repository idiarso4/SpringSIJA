package com.attendance.attendance.repository;

import com.attendance.attendance.entity.Attendance;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.stereotype.Repository;

import java.time.LocalDateTime;
import java.util.List;
import java.util.Optional;

@Repository
public interface AttendanceRepository extends JpaRepository<Attendance, Long> {
    
    List<Attendance> findByStudentId(Long studentId);

    @Query("SELECT a FROM Attendance a WHERE a.student.id = ?1 AND DATE(a.checkInTime) = CURRENT_DATE")
    Optional<Attendance> findTodayAttendance(Long studentId);

    @Query("SELECT a FROM Attendance a WHERE a.student.classRoom.id = ?1 AND a.checkInTime BETWEEN ?2 AND ?3")
    List<Attendance> findByClassAndDateRange(Long classId, LocalDateTime start, LocalDateTime end);

    @Query("SELECT COUNT(a) FROM Attendance a WHERE a.student.id = ?1 AND a.status = ?2 AND MONTH(a.checkInTime) = ?3")
    long countMonthlyAttendanceByStatus(Long studentId, String status, int month);

    @Query("SELECT a FROM Attendance a WHERE a.student.id = ?1 AND a.checkInTime BETWEEN ?2 AND ?3 ORDER BY a.checkInTime DESC")
    List<Attendance> findStudentAttendanceInRange(Long studentId, LocalDateTime start, LocalDateTime end);

    @Query("SELECT a FROM Attendance a WHERE a.status = 'LATE' AND DATE(a.checkInTime) = CURRENT_DATE")
    List<Attendance> findLateAttendancesToday();

    @Query("""
        SELECT NEW com.attendance.attendance.dto.AttendanceStats(
            a.status,
            COUNT(a),
            AVG(a.faceMatchConfidence)
        )
        FROM Attendance a
        WHERE a.student.classRoom.id = ?1
        AND a.checkInTime BETWEEN ?2 AND ?3
        GROUP BY a.status
    """)
    List<Object[]> getAttendanceStatsByClass(Long classId, LocalDateTime start, LocalDateTime end);
}
