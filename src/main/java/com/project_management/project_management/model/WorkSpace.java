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
public class WorkSpace {
    @Id
    @GeneratedValue(strategy = GenerationType.UUID)
    private String workSpace_id;
    private String title;
    private String description;
    private LocalDateTime createdOn;
    private String key;
    private boolean isLocked;
    private String logo;
    @ManyToOne
    @JoinColumn(name = "owner_id")
    private User owner;
}
