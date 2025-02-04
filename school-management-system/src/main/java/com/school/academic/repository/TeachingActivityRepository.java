package com.school.academic.repository;

import com.school.academic.entity.TeachingActivity;
import com.school.masterdata.entity.ClassRoom;
import com.school.masterdata.entity.Teacher;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.stereotype.Repository;

import java.time.LocalDate;
import java.util.List;

@Repository
public interface TeachingActivityRepository extends JpaRepository<TeachingActivity, Long> {
    
    Page<TeachingActivity> findByTeacherAndDateBetween(
        Teacher teacher, 
        LocalDate startDate, 
        LocalDate endDate, 
        Pageable pageable
    );
    
    Page<TeachingActivity> findByClassRoomAndDateBetween(
        ClassRoom classRoom, 
        LocalDate startDate, 
        LocalDate endDate, 
        Pageable pageable
    );
    
    @Query("SELECT ta FROM TeachingActivity ta WHERE ta.teacher.id = :teacherId " +
           "AND ta.date = CURRENT_DATE AND ta.deleted = false")
    List<TeachingActivity> findTodayActivitiesByTeacher(Long teacherId);
    
    @Query("SELECT COUNT(ta) FROM TeachingActivity ta WHERE ta.teacher.id = :teacherId " +
           "AND EXTRACT(MONTH FROM ta.date) = :month " +
           "AND EXTRACT(YEAR FROM ta.date) = :year " +
           "AND ta.deleted = false")
    Long countMonthlyActivitiesByTeacher(Long teacherId, int month, int year);
}
