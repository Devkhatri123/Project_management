package com.project_management.project_management.Dtos.project;

import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Size;
import org.springframework.format.annotation.DateTimeFormat;

import java.time.LocalDate;

public record CreateProjectDTO(
        @NotBlank(message = "title cannot be empty")
        @Size(min = 10, max = 300, message = "Maximum character length of title is 300. Minimum character length of title is 10 ") String title,
        @NotBlank(message = "description cannot be empty")
        @Size(min = 10, max = 300, message = "Maximum character length of description is 1024. Minimum character length of description is 10 ") String description,
        @NotNull(message = "please provide a correct and valid due date")
        @DateTimeFormat(pattern = "yyyy-MM-dd") LocalDate dueDate,
        @NotBlank(message = "workspace key is required") String work_space_key) {
}
