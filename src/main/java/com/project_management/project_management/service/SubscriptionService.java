package com.project_management.project_management.service;

import com.project_management.project_management.enums.Plan_Enums.SubscriptionStatus;
import com.project_management.project_management.enums.Plan_Enums.plan;
import com.project_management.project_management.exception.user.InvalidPlanSelected;
import com.project_management.project_management.model.Plan;
import com.project_management.project_management.model.Subscription;
import com.project_management.project_management.repository.PlanRepository;
import com.project_management.project_management.repository.SubscriptionRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.time.LocalDateTime;
import java.time.ZoneOffset;

@Service
public class SubscriptionService {
    private final PlanRepository planRepository;
    private final SubscriptionRepository subscriptionRepository;

    @Autowired
    public SubscriptionService(final PlanRepository planRepository,
                               final SubscriptionRepository subscriptionRepository){
        this.planRepository = planRepository;
        this.subscriptionRepository = subscriptionRepository;
    }
    public Subscription createSubscription(String planName) throws InvalidPlanSelected {
        return Subscription.builder()
                .plan(getPlanByName(planName))
                .startDate(LocalDateTime.now(ZoneOffset.UTC))
                .dueDate(LocalDateTime.now(ZoneOffset.UTC).plusMonths(1))
                .auto_renew(true)
                .is_cancelled(false)
                .status(SubscriptionStatus.ACTIVE)
                .build();

    }
    private Plan getPlanByName(String planName) throws InvalidPlanSelected {
        return planRepository.findByPlanName((plan.valueOf(planName)))
                .orElseThrow(() -> new InvalidPlanSelected("Invalid plan selected"));
    }
}
