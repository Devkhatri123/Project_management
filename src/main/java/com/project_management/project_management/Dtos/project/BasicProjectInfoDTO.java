package com.project_management.project_management.Dtos.project;

import lombok.Getter;
import lombok.Setter;

import java.time.Instant;

@Getter
@Setter
public class BasicProjectInfoDTO {
    private String project_id;
    private String title;
    private String description;
    private int project_progress;
    private boolean isLocked;
    private Instant createdOn;
}
