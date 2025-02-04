package com.school.academic.service;

import com.school.academic.entity.StudentPermission;
import com.school.masterdata.entity.Teacher;
import com.school.academic.repository.StudentPermissionRepository;
import com.school.security.service.NotificationService;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.LocalDateTime;
import java.util.List;

@Service
@RequiredArgsConstructor
public class StudentPermissionService {

    private final StudentPermissionRepository permissionRepository;
    private final NotificationService notificationService;

    @Transactional
    public StudentPermission createPermissionRequest(StudentPermission permission) {
        permission.setStatus(StudentPermission.PermissionStatus.PENDING);
        permission.setRequestTime(LocalDateTime.now());
        StudentPermission savedPermission = permissionRepository.save(permission);
        
        notificationService.sendPermissionRequestNotification(
            permission.getSubjectTeacher().getUser(),
            "New permission request from " + permission.getStudent().getName()
        );
        
        return savedPermission;
    }

    @Transactional
    public StudentPermission approvePermission(Long id, Teacher dutyTeacher, String remarks) {
        StudentPermission permission = permissionRepository.findById(id)
            .orElseThrow(() -> new IllegalArgumentException("Permission not found"));
            
        permission.setStatus(StudentPermission.PermissionStatus.APPROVED);
        permission.setDutyTeacher(dutyTeacher);
        permission.setRemarks(remarks);
        permission.setProcessedTime(LocalDateTime.now());
        
        notificationService.sendPermissionApprovalNotification(
            permission.getStudent().getUser(),
            "Your permission request has been approved"
        );
        
        return permissionRepository.save(permission);
    }

    @Transactional
    public StudentPermission rejectPermission(Long id, Teacher dutyTeacher, String remarks) {
        StudentPermission permission = permissionRepository.findById(id)
            .orElseThrow(() -> new IllegalArgumentException("Permission not found"));
            
        permission.setStatus(StudentPermission.PermissionStatus.REJECTED);
        permission.setDutyTeacher(dutyTeacher);
        permission.setRemarks(remarks);
        permission.setProcessedTime(LocalDateTime.now());
        
        notificationService.sendPermissionRejectionNotification(
            permission.getStudent().getUser(),
            "Your permission request has been rejected"
        );
        
        return permissionRepository.save(permission);
    }

    public List<StudentPermission> getPendingPermissionsForDutyTeacher(Teacher dutyTeacher) {
        return permissionRepository.findByDutyTeacherAndStatusOrderByRequestTimeDesc(
            dutyTeacher,
            StudentPermission.PermissionStatus.PENDING
        );
    }

    public List<StudentPermission> getPermissionsBySubjectTeacher(Teacher subjectTeacher) {
        return permissionRepository.findBySubjectTeacherAndStatusOrderByRequestTimeDesc(
            subjectTeacher,
            StudentPermission.PermissionStatus.PENDING
        );
    }

    public List<StudentPermission> getPermissionsByDateRange(LocalDateTime startTime, LocalDateTime endTime) {
        return permissionRepository.findByStatusAndRequestTimeBetweenOrderByRequestTimeDesc(
            StudentPermission.PermissionStatus.APPROVED,
            startTime,
            endTime
        );
    }
}
