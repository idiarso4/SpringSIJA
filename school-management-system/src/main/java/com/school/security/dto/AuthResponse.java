package com.school.security.dto;

import com.school.masterdata.entity.User;
import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
public class AuthResponse {
    private String token;
    private String username;
    private String email;
    private String fullName;
    private User.Role role;
}
