package com.aluminate.aluminate_global_backend.service.subscription;

import com.aluminate.aluminate_global_backend.dto.subscription.SubscriptionPlanFeatureRequest;
import com.aluminate.aluminate_global_backend.dto.subscription.SubscriptionPlanRequest;
import com.aluminate.aluminate_global_backend.model.PlanFeature;
import com.aluminate.aluminate_global_backend.model.SubscriptionPlan;
import com.aluminate.aluminate_global_backend.model.SubscriptionPlanFeature;
import com.aluminate.aluminate_global_backend.repository.FeaturesRepository;
import com.aluminate.aluminate_global_backend.repository.SubscriptionPlanRepository;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;
import java.util.stream.Collectors;

@Service
public class SubscriptionPlanService {

    private final SubscriptionPlanRepository subscriptionPlanRepository;
    private final FeaturesRepository featuresRepository;

    public SubscriptionPlanService(SubscriptionPlanRepository subscriptionPlanRepository, FeaturesRepository featuresRepository) {
        this.subscriptionPlanRepository = subscriptionPlanRepository;
        this.featuresRepository = featuresRepository;
    }

    @Transactional
    public SubscriptionPlan createSubscriptionPlan(SubscriptionPlanRequest request) {
        SubscriptionPlan plan = SubscriptionPlan.builder()
                .name(request.getName())
                .price(request.getPrice())
                .cpu(request.getCpu())
                .ram(request.getRam())
                .durationInMonths(request.getDurationInMonths())
                .memberLimit(request.getMemberLimit())
                .storageInGB(request.getStorageInGB())
                .build();

        if (request.getFeature() != null) {
            List<SubscriptionPlanFeature> featureEntities  = request.getFeature().stream()
                    .map(f -> {
                        PlanFeature planFeature = featuresRepository.findById(f.getFeatureId())
                                .orElseThrow(() -> new RuntimeException("Feature not found for ID: " + f.getFeatureId()));
                        SubscriptionPlanFeature entity = new SubscriptionPlanFeature();
                        entity.setPlanFeature(planFeature);
                        entity.setSubscriptionPlan(plan);
                        return entity;
                    }).toList();
            plan.setSubscriptionPlanFeatures(featureEntities);
        }
        return subscriptionPlanRepository.save(plan);
    }

    public List<SubscriptionPlan> getAllSubscriptionPlans() {
        return subscriptionPlanRepository.findAll();
    }



}
