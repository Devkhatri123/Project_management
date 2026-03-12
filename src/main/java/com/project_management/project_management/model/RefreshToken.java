package com.project_management.project_management.model;

import jakarta.persistence.*;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;
import lombok.RequiredArgsConstructor;

import java.time.LocalDateTime;

@Entity
@Builder
@RequiredArgsConstructor
@Getter
@AllArgsConstructor
public class RefreshToken {
    @Id
    @GeneratedValue(strategy = GenerationType.UUID)
    @Column(name = "refresh_token_id")
    private String refreshToken_id;
    private String token;
    private LocalDateTime expiresOn;
    private boolean isExpired;
    @OneToOne(mappedBy = "refreshToken")
    private User user;
}
