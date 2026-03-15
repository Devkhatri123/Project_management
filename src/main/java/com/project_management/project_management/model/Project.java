package com.project_management.project_management.model;

import jakarta.persistence.*;
import lombok.*;

import java.time.Instant;
import java.time.LocalDateTime;
import java.util.List;

@Builder
@AllArgsConstructor
@RequiredArgsConstructor
@Getter
@Setter
@Entity
public class Project {
    @Id
    @GeneratedValue(strategy = GenerationType.UUID)
    private String project_id;
    @Column(length = 300)
    private String title;
    @Column(length = 1024)
    private String description;
    private int progress_percentage;
    private Instant createdOn;
    private boolean isLocked;
    @ManyToOne(fetch = FetchType.LAZY, optional = false)
    @JoinColumn(name = "work_space_id", referencedColumnName = "workSpace_id")
    private WorkSpace workSpace;

    @OneToMany(mappedBy = "project", cascade = CascadeType.ALL, fetch = FetchType.LAZY)
    private List<Task> project_tasks;
    @ManyToMany
    @JoinTable(name = "project_assignees",
    joinColumns = {@JoinColumn(name = "project_id", referencedColumnName = "project_id")},
    inverseJoinColumns = {@JoinColumn(name = "user_id", referencedColumnName = "id")})
    private List<User> project_assignees;
}
