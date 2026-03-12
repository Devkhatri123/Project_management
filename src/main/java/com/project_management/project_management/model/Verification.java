package com.project_management.project_management.model;

import jakarta.persistence.*;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;
import lombok.RequiredArgsConstructor;

import java.time.LocalDateTime;

@Entity
@Builder
@AllArgsConstructor
@RequiredArgsConstructor
@Getter
public class Verification {
    @Id
    @GeneratedValue(strategy = GenerationType.UUID)
    private String verification_id;
    private int otpCode;
    private LocalDateTime expiresAt;
    private boolean isExpired;
    @OneToOne(mappedBy = "verification")
    private User user;
}
