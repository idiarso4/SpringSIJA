package com.school.security.dto;

import lombok.Builder;
import lombok.Data;

import java.util.Set;

@Data
@Builder
public class AuthResponse {
    private String token;
    private String username;
    private String name;
    private Set<String> roles;
}
