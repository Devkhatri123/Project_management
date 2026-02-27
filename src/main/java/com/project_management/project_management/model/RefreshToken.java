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
    private String id;
    private String token;
    private LocalDateTime expiresOn;
    private boolean isExpired;
    @OneToOne
    @MapsId
    private User user;
}
