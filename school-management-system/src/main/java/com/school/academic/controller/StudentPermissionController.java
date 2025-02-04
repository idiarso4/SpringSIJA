package com.school.academic.controller;

import com.school.academic.entity.StudentPermission;
import com.school.academic.service.StudentPermissionService;
import com.school.masterdata.entity.Teacher;
import com.school.security.annotation.CurrentUser;
import lombok.RequiredArgsConstructor;
import org.springframework.format.annotation.DateTimeFormat;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.*;

import java.time.LocalDateTime;
import java.util.List;

@RestController
@RequestMapping("/api/permissions")
@RequiredArgsConstructor
public class StudentPermissionController {

    private final StudentPermissionService permissionService;

    @PostMapping
    @PreAuthorize("hasRole('TEACHER')")
    public ResponseEntity<StudentPermission> createPermission(
            @RequestBody StudentPermission permission,
            @CurrentUser Teacher teacher) {
        permission.setSubjectTeacher(teacher);
        return ResponseEntity.ok(permissionService.createPermissionRequest(permission));
    }

    @PostMapping("/{id}/approve")
    @PreAuthorize("hasRole('TEACHER')")
    public ResponseEntity<StudentPermission> approvePermission(
            @PathVariable Long id,
            @RequestParam String remarks,
            @CurrentUser Teacher teacher) {
        return ResponseEntity.ok(permissionService.approvePermission(id, teacher, remarks));
    }

    @PostMapping("/{id}/reject")
    @PreAuthorize("hasRole('TEACHER')")
    public ResponseEntity<StudentPermission> rejectPermission(
            @PathVariable Long id,
            @RequestParam String remarks,
            @CurrentUser Teacher teacher) {
        return ResponseEntity.ok(permissionService.rejectPermission(id, teacher, remarks));
    }

    @GetMapping("/pending/duty-teacher")
    @PreAuthorize("hasRole('TEACHER')")
    public ResponseEntity<List<StudentPermission>> getPendingPermissionsForDutyTeacher(
            @CurrentUser Teacher teacher) {
        return ResponseEntity.ok(permissionService.getPendingPermissionsForDutyTeacher(teacher));
    }

    @GetMapping("/pending/subject-teacher")
    @PreAuthorize("hasRole('TEACHER')")
    public ResponseEntity<List<StudentPermission>> getPermissionsBySubjectTeacher(
            @CurrentUser Teacher teacher) {
        return ResponseEntity.ok(permissionService.getPermissionsBySubjectTeacher(teacher));
    }

    @GetMapping("/date-range")
    @PreAuthorize("hasRole('TEACHER')")
    public ResponseEntity<List<StudentPermission>> getPermissionsByDateRange(
            @RequestParam @DateTimeFormat(iso = DateTimeFormat.ISO.DATE_TIME) LocalDateTime startTime,
            @RequestParam @DateTimeFormat(iso = DateTimeFormat.ISO.DATE_TIME) LocalDateTime endTime) {
        return ResponseEntity.ok(permissionService.getPermissionsByDateRange(startTime, endTime));
    }
}
