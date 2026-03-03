package com.project_management.project_management.repository;

import com.project_management.project_management.model.Plan;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.Optional;

public interface PlanRepository extends JpaRepository<Plan, String> {
    Optional<Plan> findByPlan_name(String s);
}
