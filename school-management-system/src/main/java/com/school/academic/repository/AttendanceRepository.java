package com.school.academic.repository;

import com.school.academic.entity.Attendance;
import com.school.masterdata.entity.Student;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.stereotype.Repository;

import java.time.LocalDate;
import java.util.List;
import java.util.Map;

@Repository
public interface AttendanceRepository extends JpaRepository<Attendance, Long> {
    
    List<Attendance> findByTeachingActivityId(Long teachingActivityId);
    
    Page<Attendance> findByStudentAndTeachingActivity_DateBetween(
        Student student,
        LocalDate startDate,
        LocalDate endDate,
        Pageable pageable
    );
    
    @Query("SELECT a.status as status, COUNT(a) as count FROM Attendance a " +
           "WHERE a.teachingActivity.classRoom.id = :classRoomId " +
           "AND a.teachingActivity.date = :date " +
           "AND a.deleted = false " +
           "GROUP BY a.status")
    List<Map<String, Object>> getDailyAttendanceStats(Long classRoomId, LocalDate date);
    
    @Query("SELECT COUNT(a) FROM Attendance a " +
           "WHERE a.student.id = :studentId " +
           "AND a.status = 'ABSENT' " +
           "AND EXTRACT(MONTH FROM a.teachingActivity.date) = :month " +
           "AND EXTRACT(YEAR FROM a.teachingActivity.date) = :year " +
           "AND a.deleted = false")
    Long countMonthlyAbsencesByStudent(Long studentId, int month, int year);
}
