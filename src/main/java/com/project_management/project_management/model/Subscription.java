package com.project_management.project_management.model;


import com.project_management.project_management.enums.Plan_Enums.SubscriptionStatus;
import jakarta.persistence.*;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;
import lombok.RequiredArgsConstructor;

import java.time.LocalDateTime;

@Entity
@Builder
@AllArgsConstructor
@RequiredArgsConstructor
@Getter
public class Subscription {
    @Id
    @GeneratedValue(strategy = GenerationType.UUID)
    private String subscription_id;
    @ManyToOne(cascade = CascadeType.ALL)
    private Plan plan;
    private LocalDateTime startDate;
    private LocalDateTime dueDate;
    private LocalDateTime updated_At;
    private LocalDateTime cancelled_At;
    private boolean is_cancelled;
    private boolean auto_renew;
    @Enumerated(EnumType.STRING)
    private SubscriptionStatus status;

}
