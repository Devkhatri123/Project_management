package com.project_management.project_management.model;

import jakarta.persistence.*;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;

import java.time.Instant;


@Entity
@Builder
@NoArgsConstructor
@AllArgsConstructor
@Getter
public class ChatMessage {
    @Id
    @GeneratedValue(strategy = GenerationType.UUID)
    private String chat_id;
    @ManyToOne
    private Project project;
    @ManyToOne
    private User sender;
    @ManyToOne
    private User receiver;
    @Column(length = 2500)
    private String message;
    private Instant sentAt;
}
