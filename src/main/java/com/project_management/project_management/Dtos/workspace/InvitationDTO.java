package com.project_management.project_management.Dtos.workspace;

import jakarta.validation.constraints.NotBlank;

public record InvitationDTO (@NotBlank(message = "email cannot be empty")  String userToBeInvitedEmail, @NotBlank(message = "workspace key not found") String workspace_key){
}
