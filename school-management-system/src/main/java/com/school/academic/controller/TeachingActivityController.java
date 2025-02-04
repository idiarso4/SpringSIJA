package com.school.academic.controller;

import com.school.academic.dto.TeachingActivityDTO;
import com.school.academic.service.TeachingActivityService;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.format.annotation.DateTimeFormat;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.*;

import jakarta.validation.Valid;
import java.time.LocalDate;
import java.util.List;

@RestController
@RequestMapping("/api/v1/teaching-activities")
@RequiredArgsConstructor
public class TeachingActivityController {

    private final TeachingActivityService teachingActivityService;

    @PostMapping
    @PreAuthorize("hasRole('TEACHER')")
    public ResponseEntity<TeachingActivityDTO> createActivity(
        @Valid @RequestBody TeachingActivityDTO activityDTO
    ) {
        return ResponseEntity.ok(teachingActivityService.createActivity(activityDTO));
    }

    @PutMapping("/{id}")
    @PreAuthorize("hasRole('TEACHER')")
    public ResponseEntity<TeachingActivityDTO> updateActivity(
        @PathVariable Long id,
        @Valid @RequestBody TeachingActivityDTO activityDTO
    ) {
        return ResponseEntity.ok(teachingActivityService.updateActivity(id, activityDTO));
    }

    @DeleteMapping("/{id}")
    @PreAuthorize("hasRole('TEACHER')")
    public ResponseEntity<Void> deleteActivity(@PathVariable Long id) {
        teachingActivityService.deleteActivity(id);
        return ResponseEntity.ok().build();
    }

    @GetMapping("/{id}")
    @PreAuthorize("hasAnyRole('TEACHER', 'ADMIN')")
    public ResponseEntity<TeachingActivityDTO> getActivity(@PathVariable Long id) {
        return ResponseEntity.ok(teachingActivityService.getActivityById(id));
    }

    @GetMapping("/teacher/{teacherId}")
    @PreAuthorize("hasAnyRole('TEACHER', 'ADMIN')")
    public ResponseEntity<Page<TeachingActivityDTO>> getTeacherActivities(
        @PathVariable Long teacherId,
        @RequestParam @DateTimeFormat(iso = DateTimeFormat.ISO.DATE) LocalDate startDate,
        @RequestParam @DateTimeFormat(iso = DateTimeFormat.ISO.DATE) LocalDate endDate,
        Pageable pageable
    ) {
        return ResponseEntity.ok(teachingActivityService.getActivitiesByTeacher(
            teacherId, startDate, endDate, pageable));
    }

    @GetMapping("/class/{classRoomId}")
    @PreAuthorize("hasAnyRole('TEACHER', 'ADMIN')")
    public ResponseEntity<Page<TeachingActivityDTO>> getClassActivities(
        @PathVariable Long classRoomId,
        @RequestParam @DateTimeFormat(iso = DateTimeFormat.ISO.DATE) LocalDate startDate,
        @RequestParam @DateTimeFormat(iso = DateTimeFormat.ISO.DATE) LocalDate endDate,
        Pageable pageable
    ) {
        return ResponseEntity.ok(teachingActivityService.getActivitiesByClassRoom(
            classRoomId, startDate, endDate, pageable));
    }

    @GetMapping("/teacher/{teacherId}/today")
    @PreAuthorize("hasAnyRole('TEACHER', 'ADMIN')")
    public ResponseEntity<List<TeachingActivityDTO>> getTodayActivities(
        @PathVariable Long teacherId
    ) {
        return ResponseEntity.ok(teachingActivityService.getTodayActivitiesByTeacher(teacherId));
    }

    @GetMapping("/teacher/{teacherId}/monthly-count")
    @PreAuthorize("hasAnyRole('TEACHER', 'ADMIN')")
    public ResponseEntity<Long> getMonthlyActivityCount(
        @PathVariable Long teacherId,
        @RequestParam int month,
        @RequestParam int year
    ) {
        return ResponseEntity.ok(teachingActivityService.getMonthlyActivityCount(
            teacherId, month, year));
    }
}
