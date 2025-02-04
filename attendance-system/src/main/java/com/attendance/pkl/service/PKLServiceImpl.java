package com.attendance.pkl.service;

import com.attendance.pkl.dto.*;
import com.attendance.pkl.entity.Company;
import com.attendance.pkl.entity.DailyActivity;
import com.attendance.pkl.entity.PKLAssignment;
import com.attendance.pkl.repository.CompanyRepository;
import com.attendance.pkl.repository.DailyActivityRepository;
import com.attendance.pkl.repository.PKLAssignmentRepository;
import com.attendance.user.entity.Student;
import com.attendance.user.entity.Teacher;
import com.attendance.user.repository.StudentRepository;
import com.attendance.user.repository.TeacherRepository;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.web.multipart.MultipartFile;

import java.time.LocalDate;
import java.time.LocalDateTime;
import java.util.List;
import java.util.stream.Collectors;

@Slf4j
@Service
@RequiredArgsConstructor
public class PKLServiceImpl implements PKLService {

    private final PKLAssignmentRepository pklAssignmentRepository;
    private final DailyActivityRepository dailyActivityRepository;
    private final CompanyRepository companyRepository;
    private final StudentRepository studentRepository;
    private final TeacherRepository teacherRepository;
    private final FileStorageService fileStorageService;

    @Override
    @Transactional
    public PKLAssignmentResponse createAssignment(CreatePKLAssignmentRequest request) {
        Student student = studentRepository.findById(request.getStudentId())
                .orElseThrow(() -> new IllegalArgumentException("Student not found"));

        Teacher supervisor = teacherRepository.findById(request.getSupervisorId())
                .orElseThrow(() -> new IllegalArgumentException("Supervisor not found"));

        Company company = companyRepository.findById(request.getCompanyId())
                .orElseThrow(() -> new IllegalArgumentException("Company not found"));

        // Validate if student already has active PKL assignment
        if (pklAssignmentRepository.existsByStudentAndEndDateAfter(student, LocalDate.now())) {
            throw new IllegalStateException("Student already has active PKL assignment");
        }

        PKLAssignment assignment = PKLAssignment.builder()
                .student(student)
                .supervisor(supervisor)
                .company(company)
                .startDate(request.getStartDate())
                .endDate(request.getEndDate())
                .position(request.getPosition())
                .department(request.getDepartment())
                .status("ACTIVE")
                .build();

        assignment = pklAssignmentRepository.save(assignment);
        return mapToPKLAssignmentResponse(assignment);
    }

    @Override
    @Transactional
    public DailyActivityResponse submitDailyActivity(CreateDailyActivityRequest request) {
        PKLAssignment assignment = pklAssignmentRepository.findActiveAssignmentByStudent(request.getStudentId())
                .orElseThrow(() -> new IllegalStateException("No active PKL assignment found"));

        // Check if activity already exists for the date
        if (dailyActivityRepository.existsByAssignmentAndDate(assignment, request.getDate())) {
            throw new IllegalStateException("Activity already submitted for this date");
        }

        DailyActivity activity = DailyActivity.builder()
                .assignment(assignment)
                .date(request.getDate())
                .description(request.getDescription())
                .startTime(request.getStartTime())
                .endTime(request.getEndTime())
                .status("PENDING")
                .build();

        activity = dailyActivityRepository.save(activity);
        return mapToDailyActivityResponse(activity);
    }

    @Override
    @Transactional
    public void uploadActivityPhotos(Long activityId, List<MultipartFile> photos) {
        DailyActivity activity = dailyActivityRepository.findById(activityId)
                .orElseThrow(() -> new IllegalArgumentException("Activity not found"));

        List<String> photoUrls = photos.stream()
                .map(fileStorageService::storeFile)
                .collect(Collectors.toList());

        activity.setPhotoUrls(String.join(",", photoUrls));
        dailyActivityRepository.save(activity);
    }

    @Override
    @Transactional
    public DailyActivityResponse approveActivity(Long activityId, ApproveActivityRequest request) {
        DailyActivity activity = dailyActivityRepository.findById(activityId)
                .orElseThrow(() -> new IllegalArgumentException("Activity not found"));

        activity.setStatus(request.getApproved() ? "APPROVED" : "REJECTED");
        activity.setSupervisorNotes(request.getNotes());
        activity.setApprovedAt(LocalDateTime.now());

        activity = dailyActivityRepository.save(activity);
        return mapToDailyActivityResponse(activity);
    }

    @Override
    public List<PKLAssignmentResponse> getStudentAssignments(Long studentId) {
        return pklAssignmentRepository.findByStudentId(studentId)
                .stream()
                .map(this::mapToPKLAssignmentResponse)
                .collect(Collectors.toList());
    }

    @Override
    public List<PKLAssignmentResponse> getSupervisorAssignments(Long teacherId) {
        return pklAssignmentRepository.findBySupervisorId(teacherId)
                .stream()
                .map(this::mapToPKLAssignmentResponse)
                .collect(Collectors.toList());
    }

    @Override
    public List<DailyActivityResponse> getStudentActivities(Long studentId, LocalDate startDate, LocalDate endDate) {
        return dailyActivityRepository.findByStudentAndDateRange(studentId, startDate, endDate)
                .stream()
                .map(this::mapToDailyActivityResponse)
                .collect(Collectors.toList());
    }

    @Override
    public List<CompanyResponse> getAvailableCompanies() {
        return companyRepository.findAll()
                .stream()
                .map(this::mapToCompanyResponse)
                .collect(Collectors.toList());
    }

    @Override
    @Transactional
    public CompanyResponse registerCompany(CreateCompanyRequest request) {
        Company company = Company.builder()
                .name(request.getName())
                .address(request.getAddress())
                .city(request.getCity())
                .phone(request.getPhone())
                .email(request.getEmail())
                .website(request.getWebsite())
                .industry(request.getIndustry())
                .contactPerson(request.getContactPerson())
                .active(true)
                .build();

        company = companyRepository.save(company);
        return mapToCompanyResponse(company);
    }

    @Override
    public PKLStudentStatsResponse getStudentPKLStatistics(Long studentId) {
        // Implementation for student PKL statistics
        return PKLStudentStatsResponse.builder()
                // ... build stats
                .build();
    }

    @Override
    public PKLCompanyStatsResponse getCompanyPKLStatistics(Long companyId) {
        // Implementation for company PKL statistics
        return PKLCompanyStatsResponse.builder()
                // ... build stats
                .build();
    }

    private PKLAssignmentResponse mapToPKLAssignmentResponse(PKLAssignment assignment) {
        return PKLAssignmentResponse.builder()
                .id(assignment.getId())
                .studentId(assignment.getStudent().getId())
                .studentName(assignment.getStudent().getUser().getFullName())
                .supervisorId(assignment.getSupervisor().getId())
                .supervisorName(assignment.getSupervisor().getUser().getFullName())
                .companyId(assignment.getCompany().getId())
                .companyName(assignment.getCompany().getName())
                .startDate(assignment.getStartDate())
                .endDate(assignment.getEndDate())
                .position(assignment.getPosition())
                .department(assignment.getDepartment())
                .status(assignment.getStatus())
                .build();
    }

    private DailyActivityResponse mapToDailyActivityResponse(DailyActivity activity) {
        return DailyActivityResponse.builder()
                .id(activity.getId())
                .date(activity.getDate())
                .description(activity.getDescription())
                .startTime(activity.getStartTime())
                .endTime(activity.getEndTime())
                .status(activity.getStatus())
                .supervisorNotes(activity.getSupervisorNotes())
                .photoUrls(activity.getPhotoUrls() != null ? 
                    List.of(activity.getPhotoUrls().split(",")) : 
                    List.of())
                .build();
    }

    private CompanyResponse mapToCompanyResponse(Company company) {
        return CompanyResponse.builder()
                .id(company.getId())
                .name(company.getName())
                .address(company.getAddress())
                .city(company.getCity())
                .phone(company.getPhone())
                .email(company.getEmail())
                .website(company.getWebsite())
                .industry(company.getIndustry())
                .contactPerson(company.getContactPerson())
                .active(company.getActive())
                .build();
    }
}
