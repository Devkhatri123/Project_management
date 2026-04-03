package com.project_management.project_management.projection;

import lombok.Getter;

@Getter
public class WorkSpaceInfoDTO {
    private final String workspace_id;
    private final String title;
    private final String workspace_logo;

    public WorkSpaceInfoDTO(String workspace_id, String title, String workspace_logo) {
        this.workspace_id = workspace_id;
        this.workspace_logo = workspace_logo;
        this.title = title;
    }
}
