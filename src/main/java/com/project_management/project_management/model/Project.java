package com.project_management.project_management.model;

import jakarta.persistence.*;
import lombok.*;

import java.time.LocalDateTime;

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
    private String title;
    private String description;
    private int progress_percentage;
    private LocalDateTime startDate;
    private LocalDateTime dueDate;
    private boolean isLocked;
    @ManyToOne
    @JoinColumn(name = "work_space_id")
    private WorkSpace workSpace;
}
