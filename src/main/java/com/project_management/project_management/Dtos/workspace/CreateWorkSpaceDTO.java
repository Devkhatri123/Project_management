package com.project_management.project_management.Dtos.workspace;

import jakarta.validation.constraints.NotBlank;
import org.springframework.web.multipart.MultipartFile;

public record CreateWorkSpaceDTO(
        @NotBlank(message = "title cannot be empty") String title,
        @NotBlank(message = "description cannot be empty") String description,
        @NotBlank(message = "owner email cannot be empty") String owner_email,
        MultipartFile logo
) {
}
