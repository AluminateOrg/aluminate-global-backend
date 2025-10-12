package com.aluminate.aluminate_global_backend.controller.subscription;

import com.aluminate.aluminate_global_backend.dto.subscription.SubscriptionPlanRequest;
import com.aluminate.aluminate_global_backend.model.SubscriptionPlan;
import com.aluminate.aluminate_global_backend.service.subscription.SubscriptionPlanService;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("${api.prefix}/subscription-plan")
public class SubscriptionController {

    private final SubscriptionPlanService subscriptionPlanService;

    public SubscriptionController(SubscriptionPlanService subscriptionPlanService) {
        this.subscriptionPlanService = subscriptionPlanService;
    }

    @PostMapping("/create")
    public ResponseEntity<SubscriptionPlan> createSubscriptionPlan(@RequestBody SubscriptionPlanRequest request) {
        try {
            SubscriptionPlan createdPlan = subscriptionPlanService.createSubscriptionPlan(request);
            return ResponseEntity.ok(createdPlan);
        } catch (Exception e) {
            return ResponseEntity.status(500).build();
        }
    }

    @GetMapping
    public List<SubscriptionPlan> getAllSubscriptionPlans() {
        return subscriptionPlanService.getAllSubscriptionPlans();
    }

    @PutMapping("/update/{id}")
    public ResponseEntity<SubscriptionPlan> updateSubscriptionPlan(@PathVariable Long id, @RequestBody SubscriptionPlanRequest request) {
        try {
            SubscriptionPlan updatedPlan = subscriptionPlanService.updateSubscriptionPlan(id, request).orElse(null);
            return ResponseEntity.ok(updatedPlan);
        } catch (Exception e) {
            return ResponseEntity.status(500).build();
        }
    }

}
