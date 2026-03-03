package com.project_management.project_management.model;

import jakarta.persistence.*;
import lombok.*;

import java.time.LocalDateTime;
import java.util.List;

@Entity
@Builder
@AllArgsConstructor
@RequiredArgsConstructor
@Getter
@Setter
public class WorkSpace {
    @Id
    @GeneratedValue(strategy = GenerationType.UUID)
    private String workSpace_id;
    private String title;
    private String description;
    private LocalDateTime createdOn;
    private LocalDateTime last_updated;
    @Column(unique = true)
    private String key;
    private boolean isLocked;
    private String logo;
    @ManyToOne
    @JoinColumn(name = "owner_id")
    private User owner;

    @OneToMany(cascade = CascadeType.ALL, orphanRemoval = true)
    private List<Project> my_projects;
    @OneToMany(cascade = CascadeType.ALL, orphanRemoval = true)
    private List<User> workspace_employees;
}
