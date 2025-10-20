package com.aluminate.aluminate_global_backend.controller;


import com.aluminate.aluminate_global_backend.model.SubscriptionPlan;
import com.aluminate.aluminate_global_backend.service.subscription.SubscriptionPlanService;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import java.util.List;

@RestController
@RequestMapping("${api.prefix}/public/subscription-plan")
public class PublicProcessController {
    private final SubscriptionPlanService subscriptionPlanService;

    public PublicProcessController(SubscriptionPlanService subscriptionPlanService) {
        this.subscriptionPlanService = subscriptionPlanService;
    }
    @GetMapping
    public List<SubscriptionPlan> getAllSubscriptionPlans() {
        return subscriptionPlanService.getAllSubscriptionPlans();
    }
}
