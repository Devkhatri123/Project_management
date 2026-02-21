package com.project_management.project_management.model;

import jakarta.persistence.Entity;
import jakarta.persistence.Id;
import jakarta.persistence.JoinColumn;
import jakarta.persistence.OneToOne;
import lombok.Builder;
import lombok.Getter;
import lombok.RequiredArgsConstructor;

import java.time.LocalDateTime;

@Entity
@Builder
@RequiredArgsConstructor
@Getter
public class RefreshToken {
    @Id
    @OneToOne
    @JoinColumn(name = "user_id")
    private User user;
    private String token;
    private LocalDateTime expiresOn;
    private boolean isExpired;
}
