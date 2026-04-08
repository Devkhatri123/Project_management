package com.project_management.project_management.projection;

import com.project_management.project_management.model.Status;
import lombok.Getter;

@Getter
public class AssignedTaskInfoDTO {
    private final String task_id;
    private final String task_title;
    private final String project_title;
    private final String project_id;
    private final Status task_status;

    public AssignedTaskInfoDTO(String task_id, String task_title, String project_id, String project_title, Status task_status) {
        this.task_id = task_id;
        this.task_title = task_title;
        this.project_id = project_id;
        this.project_title = project_title;
        this.task_status = task_status;
    }
}
