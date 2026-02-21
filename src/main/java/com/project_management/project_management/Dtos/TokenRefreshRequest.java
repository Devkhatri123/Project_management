package com.project_management.project_management.Dtos;

public record TokenRefreshRequest(String token, String email, String timeZone) {
}
