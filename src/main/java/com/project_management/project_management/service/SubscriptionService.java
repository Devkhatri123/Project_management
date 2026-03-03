package com.project_management.project_management.service;

import com.project_management.project_management.enums.Plan_Enums.SubscriptionStatus;
import com.project_management.project_management.exception.user.InvalidPlanSelected;
import com.project_management.project_management.model.Plan;
import com.project_management.project_management.model.Subscription;
import com.project_management.project_management.repository.PlanRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.time.LocalDateTime;
import java.time.ZoneOffset;

@Service
public class SubscriptionService {
    private final PlanRepository planRepository;

    @Autowired
    public SubscriptionService(final PlanRepository planRepository){
        this.planRepository = planRepository;
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
        return planRepository.findByPlan_name(planName)
                .orElseThrow(() -> new InvalidPlanSelected("Invalid plan selected"));
    }
}
