package com.project_management.project_management.model;

import jakarta.persistence.*;

import java.time.LocalDateTime;

@Entity
public class WorkSpace {
    @Id
    @GeneratedValue(strategy = GenerationType.UUID)
    private String work_space_id;
    private String title;
    private String description;
    private LocalDateTime createdOn;
    private String key;
    private boolean isLocked;
    @ManyToOne
    @JoinColumn(name = "owner_id")
    private User owner;
}
