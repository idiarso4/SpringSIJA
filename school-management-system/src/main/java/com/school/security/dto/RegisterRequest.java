package com.school.security.dto;

import jakarta.validation.constraints.Email;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import lombok.Data;

import java.time.LocalDate;

@Data
public class RegisterRequest {
    @NotBlank
    private String username;

    @NotBlank
    private String password;

    @NotBlank
    @Email
    private String email;

    @NotBlank
    private String name;

    @NotBlank
    private String role;

    private String identificationNumber;
    private LocalDate birthDate;
    private Integer entryYear;
}
