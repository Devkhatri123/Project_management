package com.project_management.project_management.Dtos;

import lombok.Getter;
import lombok.Setter;


public record ForgetPasswordDTO(String newPassword, String confirmPassword, String timeZone) {
}
