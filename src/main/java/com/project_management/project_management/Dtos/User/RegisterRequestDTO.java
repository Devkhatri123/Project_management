package com.project_management.project_management.Dtos.User;

import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.Size;

public record RegisterRequestDTO(
        @NotBlank(message = "name cannot be null") String name,
        @NotBlank(message = "email cannot be null") String email,
        @NotBlank(message = "password cannot be null")
        @Size(min = 8, max = 16, message = "password should be of minimum 8 characters and maximum 16 characters")
        String password,
        @NotBlank(message = "role cannot be null") String role){}