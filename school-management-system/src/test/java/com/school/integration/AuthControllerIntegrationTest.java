package com.school.integration;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.school.security.dto.AuthRequest;
import com.school.security.dto.RegisterRequest;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.http.MediaType;
import org.springframework.test.context.ActiveProfiles;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.test.web.servlet.ResultActions;

import java.util.HashSet;
import java.util.Set;

import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.*;

@SpringBootTest
@AutoConfigureMockMvc
@ActiveProfiles("test")
class AuthControllerIntegrationTest {

    @Autowired
    private MockMvc mockMvc;

    @Autowired
    private ObjectMapper objectMapper;

    @Test
    void registerTeacher_Success() throws Exception {
        // Create registration request
        RegisterRequest request = new RegisterRequest();
        request.setUsername("teacher1");
        request.setPassword("password123");
        request.setFullName("John Doe");
        request.setEmail("john.doe@school.com");
        request.setPhoneNumber("+1234567890");
        
        Set<String> roles = new HashSet<>();
        roles.add("TEACHER");
        request.setRoles(roles);
        
        request.setEmployeeNumber("T001");
        request.setQualifications("Master's in Education");
        request.setSpecialization("Mathematics");

        // Perform registration
        ResultActions result = mockMvc.perform(post("/api/v1/auth/register")
            .contentType(MediaType.APPLICATION_JSON)
            .content(objectMapper.writeValueAsString(request)));

        // Verify response
        result.andExpect(status().isOk())
            .andExpect(jsonPath("$.token").exists())
            .andExpect(jsonPath("$.username").value("teacher1"))
            .andExpect(jsonPath("$.userType").value("TEACHER"));
    }

    @Test
    void login_Success() throws Exception {
        // First register a user
        RegisterRequest registerRequest = new RegisterRequest();
        registerRequest.setUsername("testuser");
        registerRequest.setPassword("password123");
        registerRequest.setFullName("Test User");
        registerRequest.setEmail("test@school.com");
        Set<String> roles = new HashSet<>();
        roles.add("STUDENT");
        registerRequest.setRoles(roles);

        mockMvc.perform(post("/api/v1/auth/register")
            .contentType(MediaType.APPLICATION_JSON)
            .content(objectMapper.writeValueAsString(registerRequest)));

        // Then try to login
        AuthRequest loginRequest = new AuthRequest();
        loginRequest.setUsername("testuser");
        loginRequest.setPassword("password123");

        ResultActions result = mockMvc.perform(post("/api/v1/auth/login")
            .contentType(MediaType.APPLICATION_JSON)
            .content(objectMapper.writeValueAsString(loginRequest)));

        result.andExpect(status().isOk())
            .andExpect(jsonPath("$.token").exists())
            .andExpect(jsonPath("$.username").value("testuser"));
    }

    @Test
    void register_ValidationFailure() throws Exception {
        RegisterRequest request = new RegisterRequest();
        // Missing required fields

        ResultActions result = mockMvc.perform(post("/api/v1/auth/register")
            .contentType(MediaType.APPLICATION_JSON)
            .content(objectMapper.writeValueAsString(request)));

        result.andExpect(status().isBadRequest())
            .andExpect(jsonPath("$.errors").exists());
    }

    @Test
    void login_InvalidCredentials() throws Exception {
        AuthRequest request = new AuthRequest();
        request.setUsername("nonexistent");
        request.setPassword("wrongpassword");

        ResultActions result = mockMvc.perform(post("/api/v1/auth/login")
            .contentType(MediaType.APPLICATION_JSON)
            .content(objectMapper.writeValueAsString(request)));

        result.andExpect(status().isUnauthorized());
    }
}
