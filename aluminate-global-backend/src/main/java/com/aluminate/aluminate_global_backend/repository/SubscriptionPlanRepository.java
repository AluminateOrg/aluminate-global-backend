package com.aluminate.aluminate_global_backend.repository;

import com.aluminate.aluminate_global_backend.model.SubscriptionPlan;
import org.springframework.data.jpa.repository.JpaRepository;

public interface SubscriptionPlanRepository extends JpaRepository<SubscriptionPlan, Long> {
    // Additional query methods can be defined here if needed
}
