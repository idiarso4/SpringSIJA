package com.school.academic.repository;

import com.school.academic.entity.TeachingActivity;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.time.LocalDateTime;

@Repository
public interface TeachingActivityRepository extends JpaRepository<TeachingActivity, Long> {
    Page<TeachingActivity> findByTeacherIdAndStartTimeBetween(Long teacherId, LocalDateTime startTime, LocalDateTime endTime, Pageable pageable);
    Page<TeachingActivity> findByClassRoomIdAndStartTimeBetween(Long classRoomId, LocalDateTime startTime, LocalDateTime endTime, Pageable pageable);
}
