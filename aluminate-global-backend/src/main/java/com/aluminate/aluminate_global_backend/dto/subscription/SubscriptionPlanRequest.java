package com.aluminate.aluminate_global_backend.dto.subscription;

import java.math.BigDecimal;
import java.util.List;

public class SubscriptionPlanRequest {
    private String name;
    private BigDecimal price;
    private Integer memberLimit;
    private Integer durationInMonths;
    private Integer storageInGB;
    private Integer cpu;
    private Integer ram;
    private List<SubscriptionPlanFeatureRequest> feature;
}

