package com.project_management.project_management.model;

import jakarta.persistence.*;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.RequiredArgsConstructor;

import java.time.LocalDateTime;

@Entity
@Builder
@AllArgsConstructor
@RequiredArgsConstructor
public class Verification {
    @Id
    @OneToOne
    @JoinColumn(name = "user_id")
    private User user;
    private String verificationType;
    private int otpCode;
    private LocalDateTime expiresAt;
    private boolean isExpired;

}
