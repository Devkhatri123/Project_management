package com.project_management.project_management.model;


import jakarta.persistence.*;
import lombok.Builder;
import lombok.Getter;
import lombok.RequiredArgsConstructor;
import lombok.Setter;

import java.time.LocalDateTime;

@Getter
@Setter
@Entity
@Builder
@RequiredArgsConstructor
public class ForgetPassword {
    @Id
    @OneToOne
    @JoinColumn(name = "user_id")
    private User user;
    @Column(unique = true)
    private String token;
    private LocalDateTime expiry;
    private boolean is_Active;
}
