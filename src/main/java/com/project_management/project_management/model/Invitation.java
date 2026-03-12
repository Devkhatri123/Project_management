package com.project_management.project_management.model;

import com.project_management.project_management.enums.WorkSpace_Enums.WorkSpaceJoin_Status;
import jakarta.persistence.*;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;
import lombok.RequiredArgsConstructor;

import java.time.LocalDateTime;

@Builder
@AllArgsConstructor
@RequiredArgsConstructor
@Getter
@Entity
public class Invitation {
    @Id
    @GeneratedValue(strategy = GenerationType.UUID)
    private String id;
    private String email;
    @ManyToOne
    @JoinColumn(name = "invited_workspace_id")
    private WorkSpace invitedToWorkspace;
    @Column(unique = true)
    private String link;
    private LocalDateTime expiresOn;
    @Enumerated(EnumType.STRING)
    private WorkSpaceJoin_Status status;
}
