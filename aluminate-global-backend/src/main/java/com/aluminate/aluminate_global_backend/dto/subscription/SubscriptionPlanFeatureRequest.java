package com.aluminate.aluminate_global_backend.dto.subscription;

import lombok.Data;

@Data
public class SubscriptionPlanFeatureRequest {
    private Long featureId;
    private boolean enabled;
}
