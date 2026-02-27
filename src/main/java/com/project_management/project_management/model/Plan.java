package com.project_management.project_management.model;

import com.project_management.project_management.enums.SubscriptionType;
import com.project_management.project_management.enums.plan;
import jakarta.persistence.*;

import java.math.BigDecimal;

@Entity
public class Plan {
    @Id
    @GeneratedValue(strategy = GenerationType.UUID)
    private String plan_id;
    @Enumerated(EnumType.STRING)
    private plan plan_name; // BASIC, PREMIUM
    private Integer max_work_space;
    private Integer max_members_per_workspace;
    private Integer max_projects_per_workspace;
    private Integer max_tasks_per_project;
    private Integer max_attachment_per_task;
    private boolean is_AI_Allowed;
    private BigDecimal price;
    private String currencyCode;
    private int durationInDays;
    @Enumerated(EnumType.STRING)
    private SubscriptionType subscriptionType;  // MONTHLY, YEARLY
}
