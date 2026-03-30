package com.project_management.project_management.Dtos.task;

import jakarta.validation.constraints.NotNull;

public record ChangeStatusDTO(@NotNull String newStatus) {
}
