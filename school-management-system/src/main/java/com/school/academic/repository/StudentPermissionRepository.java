package com.school.academic.repository;

import com.school.academic.entity.StudentPermission;
import com.school.masterdata.entity.Teacher;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.time.LocalDateTime;
import java.util.List;

@Repository
public interface StudentPermissionRepository extends JpaRepository<StudentPermission, Long> {
    
    List<StudentPermission> findBySubjectTeacherAndStatusOrderByRequestTimeDesc(
        Teacher subjectTeacher, 
        StudentPermission.PermissionStatus status
    );
    
    List<StudentPermission> findByDutyTeacherAndStatusOrderByRequestTimeDesc(
        Teacher dutyTeacher, 
        StudentPermission.PermissionStatus status
    );
    
    List<StudentPermission> findByStudentIdAndRequestTimeBetweenOrderByRequestTimeDesc(
        Long studentId, 
        LocalDateTime startDate, 
        LocalDateTime endDate
    );
    
    List<StudentPermission> findByStatusAndRequestTimeBefore(
        StudentPermission.PermissionStatus status, 
        LocalDateTime time
    );
    
    List<StudentPermission> findByStatusAndRequestTimeBetweenOrderByRequestTimeDesc(
        StudentPermission.PermissionStatus status,
        LocalDateTime startTime,
        LocalDateTime endTime
    );
}
