package com.project_management.project_management.model;

import com.fasterxml.jackson.annotation.JsonIgnore;
import jakarta.persistence.*;
import lombok.*;

import java.time.Instant;

@Entity
@Builder
@AllArgsConstructor
@RequiredArgsConstructor
@Getter
@Setter
public class Attachment {
    @Id
    @GeneratedValue(strategy = GenerationType.UUID)
    private String attachment_id;
    @Column(length = 150)
    private String attachment_name;
    private String attachment_format;
    private String attachment_type;
    private String attachment_url;
    private Instant uploadedOn;
    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "task_id")
    @JsonIgnore
    private Task attachment_Owner;


}
