package com.project_management.project_management.Dtos.workspace;

import jakarta.validation.constraints.NotBlank;
import org.springframework.web.multipart.MultipartFile;

public record UpdateWorkSpace(@NotBlank(message="title cannot be empty") String title, @NotBlank(message = "description cannot be empty") String description, MultipartFile logo, @NotBlank(message = "work_space cannot be empty") String workspace_key) {
}
