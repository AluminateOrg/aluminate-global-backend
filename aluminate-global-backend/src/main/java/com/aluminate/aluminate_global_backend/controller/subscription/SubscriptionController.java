package com.aluminate.aluminate_global_backend.controller.subscription;

import com.aluminate.aluminate_global_backend.dto.subscription.SubscriptionPlanRequest;
import com.aluminate.aluminate_global_backend.model.SubscriptionPlan;
import com.aluminate.aluminate_global_backend.service.subscription.SubscriptionPlanService;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("${api.prefix}/superAdmin/subscription-plan")
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

    @PutMapping("/update/{id}")
    public ResponseEntity<SubscriptionPlan> updateSubscriptionPlan(@PathVariable Long id, @RequestBody SubscriptionPlanRequest request) {
        try {
            SubscriptionPlan updatedPlan = subscriptionPlanService.updateSubscriptionPlan(id, request).orElse(null);
            return ResponseEntity.ok(updatedPlan);
        } catch (Exception e) {
            return ResponseEntity.status(500).build();
        }
    }

    @GetMapping("/delete/{id}")
    public ResponseEntity<Boolean> deleteSubscriptionPlan(@PathVariable Long id) {
        try {
            boolean deleted = subscriptionPlanService.deleteSubscriptionPlan(id);
            return ResponseEntity.ok(deleted);
        } catch (Exception e) {
            return ResponseEntity.status(500).build();
        }
    }

    @GetMapping
    public ResponseEntity<List<SubscriptionPlan>> getSubscriptionPlans() {
        try {
            List<SubscriptionPlan> subscriptionPlans = subscriptionPlanService.getAllSubscriptionPlans();
            return ResponseEntity.ok(subscriptionPlans);
        } catch (Exception e) {
            throw new RuntimeException(e);
        }
    }

}
