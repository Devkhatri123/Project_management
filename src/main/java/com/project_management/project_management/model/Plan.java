package com.project_management.project_management.model;

import com.project_management.project_management.enums.Plan_Enums.CurrencyCode;
import com.project_management.project_management.enums.Plan_Enums.SubscriptionType;
import com.project_management.project_management.enums.Plan_Enums.plan;
import jakarta.persistence.*;
import lombok.AllArgsConstructor;
import lombok.Getter;

import java.math.BigDecimal;

@Entity
@Getter
public class Plan {
    @Id
    @GeneratedValue(strategy = GenerationType.UUID)
    private String plan_id;
    private String product_id;
    @Enumerated(EnumType.STRING)
    @Column(unique = true)
    private plan plan_name; // BASIC, PREMIUM
    private Integer max_work_space;
    private Integer max_members_per_workspace;
    private Integer max_projects_per_workspace;
    private Integer max_tasks_per_project;
    private Integer max_attachment_per_task;
    private boolean is_AI_Allowed;
    private BigDecimal price;
    @Enumerated(EnumType.STRING)
    private CurrencyCode currencyCode;
    private int durationInDays;
    @Enumerated(EnumType.STRING)
    private SubscriptionType subscriptionType;  // MONTHLY, YEARLY
}
