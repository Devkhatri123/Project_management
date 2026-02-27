package com.project_management.project_management.model;


import jakarta.persistence.*;
import lombok.*;

import java.time.LocalDateTime;

@Getter
@Setter
@Entity
@Builder
@RequiredArgsConstructor
@AllArgsConstructor
public class ForgetPassword {
    @Id
    private String id;
    @Column(unique = true)
    private String token;
    private LocalDateTime expiry;
    private boolean is_Active;
    @OneToOne
    @MapsId
    private User user;
}
