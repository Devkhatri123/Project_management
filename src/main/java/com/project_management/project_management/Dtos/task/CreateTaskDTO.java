package com.project_management.project_management.Dtos.task;

import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Size;
import org.springframework.format.annotation.DateTimeFormat;

import java.time.LocalDate;
import java.util.List;

public record CreateTaskDTO(
        @NotBlank(message = "Title cannot be empty")
        @Size(min = 20, max = 250, message = "Title's min character length is 20 and max character length is 250") String title,
        @NotBlank(message = "Description cannot be empty")
        @Size(min = 20, max = 250, message = "Description min character length is 20 and max character length is 2048")String description,
        @NotNull(message = "Select a startDate") @DateTimeFormat(pattern = "yyyy-MM-dd") LocalDate startDate,
        @NotNull(message = "Select a startDate") @DateTimeFormat(pattern = "yyyy-MM-dd")  LocalDate dueDate,
        @NotBlank(message = "Project id cannot be empty") String project_id,
        @NotBlank(message = "Select an assignee") String assignee_email,
        @NotBlank(message = "Please select status for the task") String status,
        @NotNull(message = "Please select minimum 1 tag for the task") List<String> tags) {
}
