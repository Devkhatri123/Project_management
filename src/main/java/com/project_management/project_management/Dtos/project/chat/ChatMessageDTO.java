package com.project_management.project_management.Dtos.project.chat;

import lombok.Getter;

@Getter
public class ChatMessageDTO {
    private String senderId;
    private String receiverId;
    private String message;
    private String projectId;
    private String workSpaceNameId;
}
