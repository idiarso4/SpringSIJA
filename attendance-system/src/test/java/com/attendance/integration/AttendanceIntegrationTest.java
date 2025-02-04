package com.attendance.integration;

import com.attendance.attendance.dto.AttendanceResponse;
import com.attendance.attendance.dto.CheckInRequest;
import com.attendance.user.entity.Student;
import com.attendance.user.entity.User;
import com.attendance.user.repository.StudentRepository;
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

import static org.junit.jupiter.api.Assertions.*;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.*;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.*;

@SpringBootTest
@AutoConfigureMockMvc
@ActiveProfiles("test")
@Transactional
public class AttendanceIntegrationTest {

    @Autowired
    private MockMvc mockMvc;

    @Autowired
    private ObjectMapper objectMapper;

    @Autowired
    private UserRepository userRepository;

    @Autowired
    private StudentRepository studentRepository;

    private User user;
    private Student student;
    private String authToken;

    @BeforeEach
    void setUp() {
        // Create test user
        user = User.builder()
                .username("testuser")
                .email("test@example.com")
                .password("password123")
                .fullName("Test User")
                .roles("ROLE_STUDENT")
                .active(true)
                .build();
        userRepository.save(user);

        // Create test student
        student = Student.builder()
                .user(user)
                .studentNumber("S12345")
                .faceRegistered(true)
                .faceEncodingData("test-encoding-data")
                .build();
        studentRepository.save(student);

        // Get authentication token
        // Note: In a real test, you would call the auth endpoint
        authToken = "Bearer test-token";
    }

    @Test
    @WithMockUser(username = "testuser", roles = "STUDENT")
    void testCheckInFlow() throws Exception {
        // Create mock face image
        MockMultipartFile faceImage = new MockMultipartFile(
            "faceImage",
            "test-face.jpg",
            MediaType.IMAGE_JPEG_VALUE,
            "test-image-data".getBytes()
        );

        // Create check-in request
        CheckInRequest checkInRequest = CheckInRequest.builder()
            .studentId(student.getId())
            .latitude(1.234)
            .longitude(4.567)
            .accuracy(10.0)
            .location("Test Location")
            .deviceInfo("Test Device")
            .ipAddress("127.0.0.1")
            .build();

        // Perform check-in
        MvcResult result = mockMvc.perform(multipart("/api/v1/attendance/check-in")
                .file(faceImage)
                .param("request", objectMapper.writeValueAsString(checkInRequest))
                .header("Authorization", authToken)
                .contentType(MediaType.MULTIPART_FORM_DATA))
            .andExpect(status().isOk())
            .andExpect(content().contentType(MediaType.APPLICATION_JSON))
            .andReturn();

        // Parse response
        AttendanceResponse response = objectMapper.readValue(
            result.getResponse().getContentAsString(),
            AttendanceResponse.class
        );

        // Verify response
        assertNotNull(response);
        assertEquals(student.getUser().getFullName(), response.getStudentName());
        assertNotNull(response.getCheckInTime());
        assertTrue(response.getIsValidLocation());
    }

    @Test
    @WithMockUser(username = "testuser", roles = "STUDENT")
    void testGetAttendanceHistory() throws Exception {
        // Get attendance history
        mockMvc.perform(get("/api/v1/attendance/history")
                .param("studentId", student.getId().toString())
                .param("startDate", "2025-02-01")
                .param("endDate", "2025-02-28")
                .header("Authorization", authToken))
            .andExpect(status().isOk())
            .andExpect(content().contentType(MediaType.APPLICATION_JSON))
            .andExpect(jsonPath("$.content").isArray());
    }

    @Test
    @WithMockUser(username = "testuser", roles = "STUDENT")
    void testInvalidLocation() throws Exception {
        // Create mock face image with invalid location
        MockMultipartFile faceImage = new MockMultipartFile(
            "faceImage",
            "test-face.jpg",
            MediaType.IMAGE_JPEG_VALUE,
            "test-image-data".getBytes()
        );

        // Create check-in request with invalid location
        CheckInRequest checkInRequest = CheckInRequest.builder()
            .studentId(student.getId())
            .latitude(90.0) // Invalid latitude
            .longitude(180.0) // Invalid longitude
            .accuracy(10.0)
            .location("Invalid Location")
            .deviceInfo("Test Device")
            .ipAddress("127.0.0.1")
            .build();

        // Perform check-in
        MvcResult result = mockMvc.perform(multipart("/api/v1/attendance/check-in")
                .file(faceImage)
                .param("request", objectMapper.writeValueAsString(checkInRequest))
                .header("Authorization", authToken)
                .contentType(MediaType.MULTIPART_FORM_DATA))
            .andExpect(status().isOk())
            .andReturn();

        // Parse response
        AttendanceResponse response = objectMapper.readValue(
            result.getResponse().getContentAsString(),
            AttendanceResponse.class
        );

        // Verify response
        assertNotNull(response);
        assertFalse(response.getIsValidLocation());
    }

    @Test
    @WithMockUser(username = "testuser", roles = "STUDENT")
    void testDuplicateCheckIn() throws Exception {
        // Create mock face image
        MockMultipartFile faceImage = new MockMultipartFile(
            "faceImage",
            "test-face.jpg",
            MediaType.IMAGE_JPEG_VALUE,
            "test-image-data".getBytes()
        );

        // Create check-in request
        CheckInRequest checkInRequest = CheckInRequest.builder()
            .studentId(student.getId())
            .latitude(1.234)
            .longitude(4.567)
            .accuracy(10.0)
            .location("Test Location")
            .deviceInfo("Test Device")
            .ipAddress("127.0.0.1")
            .build();

        // First check-in
        mockMvc.perform(multipart("/api/v1/attendance/check-in")
                .file(faceImage)
                .param("request", objectMapper.writeValueAsString(checkInRequest))
                .header("Authorization", authToken)
                .contentType(MediaType.MULTIPART_FORM_DATA))
            .andExpect(status().isOk());

        // Second check-in (should fail)
        mockMvc.perform(multipart("/api/v1/attendance/check-in")
                .file(faceImage)
                .param("request", objectMapper.writeValueAsString(checkInRequest))
                .header("Authorization", authToken)
                .contentType(MediaType.MULTIPART_FORM_DATA))
            .andExpect(status().isBadRequest());
    }
}
