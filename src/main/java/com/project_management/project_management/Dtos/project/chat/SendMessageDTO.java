package com.project_management.project_management.Dtos.project.chat;

import lombok.Getter;

@Getter
public class SendMessageDTO {
    private String sender_id;
    private String receiver_id;
    private String project_id;
    private String message;
}
