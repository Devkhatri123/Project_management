package com.project_management.project_management.Dtos;

import jakarta.validation.constraints.NotBlank;
import org.springframework.web.multipart.MultipartFile;

public record CreateWorkSpaceDTO(
        @NotBlank String title,
        @NotBlank String description,
        @NotBlank String owner_email,
        MultipartFile logo
) {
}
