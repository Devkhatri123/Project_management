package com.project_management.project_management.Dtos.project.chat;

import lombok.Getter;
import lombok.Setter;

import java.time.Instant;

@Getter
@Setter
public class ReceiveMessageDTO {
    private String message;
    private String sender_id;
    private String sender_name;
    private String sender_job_title;
    private String sender_profile_pic;
    private Instant sentAt;
}
