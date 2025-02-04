package com.attendance.integration;

import com.attendance.pkl.dto.CreatePKLAssignmentRequest;
import com.attendance.pkl.dto.DailyActivityRequest;
import com.attendance.pkl.dto.PKLAssignmentResponse;
import com.attendance.pkl.entity.Company;
import com.attendance.pkl.repository.CompanyRepository;
import com.attendance.user.entity.Student;
import com.attendance.user.entity.Teacher;
import com.attendance.user.entity.User;
import com.attendance.user.repository.StudentRepository;
import com.attendance.user.repository.TeacherRepository;
import com.attendance.user.repository.UserRepository;
import com.fasterxml.jackson.databind.ObjectMapper;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.http.MediaType;
import org.springframework.mock.web.MockMultipartFile;
import org.springframework.security.test.context.support.WithMockUser;
import org.springframework.test.context.ActiveProfiles;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.test.web.servlet.MvcResult;
import org.springframework.transaction.annotation.Transactional;

import java.time.LocalDate;
import java.time.LocalTime;
import java.util.List;

import static org.junit.jupiter.api.Assertions.*;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.*;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.*;

@SpringBootTest
@AutoConfigureMockMvc
@ActiveProfiles("test")
@Transactional
public class PKLIntegrationTest {

    @Autowired
    private MockMvc mockMvc;

    @Autowired
    private ObjectMapper objectMapper;

    @Autowired
    private UserRepository userRepository;

    @Autowired
    private StudentRepository studentRepository;

    @Autowired
    private TeacherRepository teacherRepository;

    @Autowired
    private CompanyRepository companyRepository;

    private Student student;
    private Teacher teacher;
    private Company company;
    private String authToken;

    @BeforeEach
    void setUp() {
        // Create test user for student
        User studentUser = User.builder()
                .username("student")
                .email("student@example.com")
                .password("password123")
                .fullName("Student User")
                .roles("ROLE_STUDENT")
                .active(true)
                .build();
        userRepository.save(studentUser);

        // Create test user for teacher
        User teacherUser = User.builder()
                .username("teacher")
                .email("teacher@example.com")
                .password("password123")
                .fullName("Teacher User")
                .roles("ROLE_TEACHER")
                .active(true)
                .build();
        userRepository.save(teacherUser);

        // Create test student
        student = Student.builder()
                .user(studentUser)
                .studentNumber("S12345")
                .build();
        studentRepository.save(student);

        // Create test teacher
        teacher = Teacher.builder()
                .user(teacherUser)
                .teacherNumber("T12345")
                .specialization("Computer Science")
                .build();
        teacherRepository.save(teacher);

        // Create test company
        company = Company.builder()
                .name("Test Company")
                .address("Test Address")
                .city("Test City")
                .phone("123-456-7890")
                .email("company@example.com")
                .industry("Technology")
                .active(true)
                .build();
        companyRepository.save(company);

        // Get authentication token
        authToken = "Bearer test-token";
    }

    @Test
    @WithMockUser(username = "teacher", roles = "TEACHER")
    void testCreatePKLAssignment() throws Exception {
        // Create PKL assignment request
        CreatePKLAssignmentRequest request = CreatePKLAssignmentRequest.builder()
                .studentId(student.getId())
                .supervisorId(teacher.getId())
                .companyId(company.getId())
                .startDate(LocalDate.now())
                .endDate(LocalDate.now().plusMonths(3))
                .position("Software Developer Intern")
                .department("Engineering")
                .build();

        // Create assignment
        MvcResult result = mockMvc.perform(post("/api/v1/pkl/assignments")
                .header("Authorization", authToken)
                .contentType(MediaType.APPLICATION_JSON)
                .content(objectMapper.writeValueAsString(request)))
            .andExpect(status().isOk())
            .andExpect(content().contentType(MediaType.APPLICATION_JSON))
            .andReturn();

        // Parse response
        PKLAssignmentResponse response = objectMapper.readValue(
            result.getResponse().getContentAsString(),
            PKLAssignmentResponse.class
        );

        // Verify response
        assertNotNull(response);
        assertEquals(student.getId(), response.getStudentId());
        assertEquals(teacher.getId(), response.getSupervisorId());
        assertEquals(company.getId(), response.getCompanyId());
        assertEquals("ACTIVE", response.getStatus());
    }

    @Test
    @WithMockUser(username = "student", roles = "STUDENT")
    void testSubmitDailyActivity() throws Exception {
        // First create PKL assignment
        CreatePKLAssignmentRequest assignmentRequest = CreatePKLAssignmentRequest.builder()
                .studentId(student.getId())
                .supervisorId(teacher.getId())
                .companyId(company.getId())
                .startDate(LocalDate.now())
                .endDate(LocalDate.now().plusMonths(3))
                .position("Software Developer Intern")
                .department("Engineering")
                .build();

        MvcResult assignmentResult = mockMvc.perform(post("/api/v1/pkl/assignments")
                .header("Authorization", "Bearer test-teacher-token")
                .contentType(MediaType.APPLICATION_JSON)
                .content(objectMapper.writeValueAsString(assignmentRequest)))
            .andExpect(status().isOk())
            .andReturn();

        PKLAssignmentResponse assignment = objectMapper.readValue(
            assignmentResult.getResponse().getContentAsString(),
            PKLAssignmentResponse.class
        );

        // Create activity photos
        MockMultipartFile photo1 = new MockMultipartFile(
            "photos",
            "activity1.jpg",
            MediaType.IMAGE_JPEG_VALUE,
            "test-image-1".getBytes()
        );

        MockMultipartFile photo2 = new MockMultipartFile(
            "photos",
            "activity2.jpg",
            MediaType.IMAGE_JPEG_VALUE,
            "test-image-2".getBytes()
        );

        // Create daily activity request
        DailyActivityRequest activityRequest = DailyActivityRequest.builder()
                .assignmentId(assignment.getId())
                .date(LocalDate.now())
                .description("Implemented new features")
                .startTime(LocalTime.of(9, 0))
                .endTime(LocalTime.of(17, 0))
                .build();

        // Submit activity
        mockMvc.perform(multipart("/api/v1/pkl/activities")
                .file(photo1)
                .file(photo2)
                .param("request", objectMapper.writeValueAsString(activityRequest))
                .header("Authorization", authToken)
                .contentType(MediaType.MULTIPART_FORM_DATA))
            .andExpect(status().isOk())
            .andExpect(jsonPath("$.status").value("PENDING_APPROVAL"));
    }

    @Test
    @WithMockUser(username = "teacher", roles = "TEACHER")
    void testApproveActivity() throws Exception {
        // Setup PKL assignment and activity first
        // ... (similar to testSubmitDailyActivity)

        // Approve activity
        mockMvc.perform(patch("/api/v1/pkl/activities/{activityId}/approve", 1L)
                .header("Authorization", authToken)
                .param("notes", "Good work!")
                .contentType(MediaType.APPLICATION_JSON))
            .andExpect(status().isOk())
            .andExpect(jsonPath("$.status").value("APPROVED"))
            .andExpect(jsonPath("$.supervisorNotes").value("Good work!"));
    }

    @Test
    @WithMockUser(username = "student", roles = "STUDENT")
    void testGetStudentPKLProgress() throws Exception {
        mockMvc.perform(get("/api/v1/pkl/progress")
                .param("studentId", student.getId().toString())
                .header("Authorization", authToken))
            .andExpect(status().isOk())
            .andExpect(content().contentType(MediaType.APPLICATION_JSON))
            .andExpect(jsonPath("$.totalDays").exists())
            .andExpect(jsonPath("$.completedDays").exists())
            .andExpect(jsonPath("$.approvedActivities").exists());
    }
}
