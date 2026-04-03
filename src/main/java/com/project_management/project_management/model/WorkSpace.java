package com.project_management.project_management.model;

import jakarta.persistence.*;
import lombok.*;

import java.time.LocalDateTime;
import java.util.List;
import java.util.Objects;

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
    @Column(unique = true, name = "workspace_key")
    private String key;
    private boolean isLocked;
    @Column(length = 2048)
    private String logo;
    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "owner_id")
    private User owner;

    @OneToOne(mappedBy = "WorkSpace")
    private User last_accessed_by;

    @OneToMany(mappedBy = "workSpace", cascade = CascadeType.ALL, orphanRemoval = true, fetch = FetchType.LAZY)
    private List<Project> my_projects;
    @ManyToMany(cascade = CascadeType.ALL, fetch = FetchType.LAZY)
    @JoinTable(name = "workspace_employees",
    joinColumns = {@JoinColumn(name = "workspace_id", referencedColumnName = "workSpace_id")},
    inverseJoinColumns = {@JoinColumn(name = "user_id", referencedColumnName = "id")})
    private List<User> workspace_employees;
    @OneToMany(mappedBy = "invitedToWorkspace", cascade = CascadeType.ALL, fetch = FetchType.LAZY)
    private List<Invitation> invitedUsers;

    @Override
    public boolean equals(Object o) {
        if (o == null || getClass() != o.getClass()) return false;
        WorkSpace workSpace = (WorkSpace) o;
        return Objects.equals(workSpace_id, workSpace.workSpace_id);
    }

    @Override
    public int hashCode() {
        return Objects.hashCode(workSpace_id);
    }
}
