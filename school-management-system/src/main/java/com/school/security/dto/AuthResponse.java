package com.school.security.dto;

import com.school.security.entity.Role;
import lombok.Builder;
import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
@Builder
public class AuthResponse {
    private String token;
    private String username;
    private String email;
    private String fullName;
    private Role role;
}
