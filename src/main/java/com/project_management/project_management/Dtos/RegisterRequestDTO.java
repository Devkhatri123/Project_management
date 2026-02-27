package com.project_management.project_management.Dtos;

import jakarta.validation.constraints.NotBlank;

public record RegisterRequestDTO(
        @NotBlank(message = "name cannot be null") String name,
        @NotBlank(message = "email cannot be null") String email,
        @NotBlank(message = "password cannot be null") String password,
        @NotBlank(message = "role cannot be null") String role){}