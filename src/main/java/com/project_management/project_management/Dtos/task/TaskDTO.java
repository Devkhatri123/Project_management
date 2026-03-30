package com.project_management.project_management.Dtos.task;

import com.project_management.project_management.model.Attachment;
import com.project_management.project_management.model.Status;
import com.project_management.project_management.model.Tag;
import lombok.Getter;
import lombok.Setter;

import java.time.Instant;
import java.util.ArrayList;
import java.util.HashSet;
import java.util.List;
import java.util.Set;

@Setter
@Getter
public class TaskDTO {
    private String task_id;
    private String title;
    private String description;
    private Instant startDate;
    private Instant dueDate;
    private List<Attachment> task_attachments = new ArrayList<>();
    private TaskUserDTO creator;
    private TaskUserDTO assignee;
    private Set<Tag> task_tags = new HashSet<>();
    private Status task_status;
}
