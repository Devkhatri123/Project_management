package com.project_management.project_management.model;

import jakarta.persistence.*;
import lombok.*;

import java.time.Instant;
import java.util.Objects;
import java.util.Set;

@Entity
@AllArgsConstructor
@RequiredArgsConstructor
@Builder
@Getter
@Setter
public class Task {
    @Id
    @GeneratedValue(strategy = GenerationType.UUID)
    private String task_id;
    @Column(length = 250)
    private String title;
    @Column(length = 2048)
    private String description;
    private Instant startDate;
    private Instant dueDate;
    @ManyToOne(fetch = FetchType.LAZY)
    private Project project;
    @ManyToOne(fetch = FetchType.LAZY)
    private User assignee;
    @ManyToOne(fetch = FetchType.LAZY)
    private User createdBy;

    @OneToOne(mappedBy = "task")
    private Status task_status;

    @ManyToMany
    @JoinTable(name = "task_tags",
            joinColumns = {@JoinColumn(name = "task_id", referencedColumnName = "task_id")},
            inverseJoinColumns = {@JoinColumn(name = "tag_id", referencedColumnName = "tag_id")})
    private Set<Tag> task_tags;

    @Override
    public boolean equals(Object o) {
        if (o == null || getClass() != o.getClass()) return false;
        Task task = (Task) o;
        return Objects.equals(task_id, task.task_id);
    }

    @Override
    public int hashCode() {
        return Objects.hashCode(task_id);
    }
}
