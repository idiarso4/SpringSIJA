package com.school.integration;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.school.academic.dto.TeachingActivityDTO;
import com.school.academic.repository.TeachingActivityRepository;
import com.school.security.dto.AuthRequest;
import com.school.security.dto.AuthResponse;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.http.MediaType;
import org.springframework.test.context.ActiveProfiles;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.test.web.servlet.MvcResult;

import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.*;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.*;

@SpringBootTest
@AutoConfigureMockMvc
@ActiveProfiles("test")
class TeachingActivityControllerIntegrationTest {

    @Autowired
    private MockMvc mockMvc;

    @Autowired
    private ObjectMapper objectMapper;

    @Autowired
    private TeachingActivityRepository activityRepository;

    private String teacherToken;

    @BeforeEach
    void setUp() throws Exception {
        // Login as teacher to get token
        AuthRequest authRequest = new AuthRequest("teacher1", "password123");
        String authJson = objectMapper.writeValueAsString(authRequest);

        MvcResult authResult = mockMvc.perform(post("/api/v1/auth/login")
            .contentType(MediaType.APPLICATION_JSON)
            .content(authJson))
            .andReturn();

        AuthResponse authResponse = objectMapper.readValue(
            authResult.getResponse().getContentAsString(),
            AuthResponse.class
        );
        teacherToken = authResponse.getToken();
    }

    @Test
    void createTeachingActivity_Success() throws Exception {
        TeachingActivityDTO dto = new TeachingActivityDTO();
        dto.setTeacherId(1L);
        dto.setClassRoomId(1L);
        dto.setSubjectId(1L);
        dto.setTopic("Test Topic");
        dto.setDescription("Test Description");

        mockMvc.perform(post("/api/v1/teaching-activities")
            .header("Authorization", "Bearer " + teacherToken)
            .contentType(MediaType.APPLICATION_JSON)
            .content(objectMapper.writeValueAsString(dto)))
            .andExpect(status().isOk())
            .andExpect(jsonPath("$.id").exists())
            .andExpect(jsonPath("$.topic").value("Test Topic"))
            .andExpect(jsonPath("$.teacherName").exists());
    }

    @Test
    void createTeachingActivity_Unauthorized() throws Exception {
        mockMvc.perform(post("/api/v1/teaching-activities")
            .contentType(MediaType.APPLICATION_JSON)
            .content(objectMapper.writeValueAsString(new TeachingActivityDTO())))
            .andExpect(status().isUnauthorized());
    }

    @Test
    void getTeachingActivity_Success() throws Exception {
        // First create an activity
        MvcResult createResult = mockMvc.perform(post("/api/v1/teaching-activities")
            .header("Authorization", "Bearer " + teacherToken)
            .contentType(MediaType.APPLICATION_JSON)
            .content(objectMapper.writeValueAsString(new TeachingActivityDTO())))
            .andReturn();

        TeachingActivityDTO createdActivity = objectMapper.readValue(
            createResult.getResponse().getContentAsString(),
            TeachingActivityDTO.class
        );

        // Then retrieve it
        mockMvc.perform(get("/api/v1/teaching-activities/{id}", createdActivity.getId())
            .header("Authorization", "Bearer " + teacherToken))
            .andExpect(status().isOk())
            .andExpect(jsonPath("$.id").value(createdActivity.getId()));
    }

    @Test
    void updateTeachingActivity_Success() throws Exception {
        // First create an activity
        MvcResult createResult = mockMvc.perform(post("/api/v1/teaching-activities")
            .header("Authorization", "Bearer " + teacherToken)
            .contentType(MediaType.APPLICATION_JSON)
            .content(objectMapper.writeValueAsString(new TeachingActivityDTO())))
            .andReturn();

        TeachingActivityDTO createdActivity = objectMapper.readValue(
            createResult.getResponse().getContentAsString(),
            TeachingActivityDTO.class
        );

        // Update the activity
        TeachingActivityDTO dto = new TeachingActivityDTO();
        dto.setTeacherId(1L);
        dto.setClassRoomId(1L);
        dto.setSubjectId(1L);
        dto.setTopic("Updated Topic");
        dto.setDescription("Updated Description");

        mockMvc.perform(put("/api/v1/teaching-activities/{id}", createdActivity.getId())
            .header("Authorization", "Bearer " + teacherToken)
            .contentType(MediaType.APPLICATION_JSON)
            .content(objectMapper.writeValueAsString(dto)))
            .andExpect(status().isOk())
            .andExpect(jsonPath("$.topic").value("Updated Topic"));
    }

    @Test
    void deleteTeachingActivity_Success() throws Exception {
        // First create an activity
        MvcResult createResult = mockMvc.perform(post("/api/v1/teaching-activities")
            .header("Authorization", "Bearer " + teacherToken)
            .contentType(MediaType.APPLICATION_JSON)
            .content(objectMapper.writeValueAsString(new TeachingActivityDTO())))
            .andReturn();

        TeachingActivityDTO createdActivity = objectMapper.readValue(
            createResult.getResponse().getContentAsString(),
            TeachingActivityDTO.class
        );

        // Delete the activity
        mockMvc.perform(delete("/api/v1/teaching-activities/{id}", createdActivity.getId())
            .header("Authorization", "Bearer " + teacherToken))
            .andExpect(status().isOk());

        // Verify it's deleted
        mockMvc.perform(get("/api/v1/teaching-activities/{id}", createdActivity.getId())
            .header("Authorization", "Bearer " + teacherToken))
            .andExpect(status().isNotFound());
    }
}
