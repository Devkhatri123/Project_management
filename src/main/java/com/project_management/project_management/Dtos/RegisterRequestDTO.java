package com.project_management.project_management.Dtos;

import lombok.Getter;
import lombok.Setter;

public record RegisterRequestDTO(String name, String email, String password, String usingAs){}