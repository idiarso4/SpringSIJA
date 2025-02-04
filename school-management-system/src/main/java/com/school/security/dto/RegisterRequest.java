package com.school.security.dto;

import jakarta.validation.constraints.Email;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Pattern;
import lombok.Data;

import java.time.LocalDate;

@Data
public class RegisterRequest {

    @NotBlank(message = "Username is required")
    private String username;

    @NotBlank(message = "Password is required")
    private String password;

    @NotBlank(message = "Email is required")
    @Email(message = "Invalid email format")
    private String email;

    @NotBlank(message = "Name is required")
    private String name;

    @NotBlank(message = "Role is required")
    @Pattern(regexp = "^ROLE_(STUDENT|TEACHER)$", message = "Role must be either ROLE_STUDENT or ROLE_TEACHER")
    private String role;

    @NotBlank(message = "Identification number is required")
    private String identificationNumber;

    @NotNull(message = "Birth date is required")
    private LocalDate birthDate;

    @NotNull(message = "Entry year is required")
    private Integer entryYear;

    private Long classRoomId;
}
