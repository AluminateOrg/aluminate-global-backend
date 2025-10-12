package com.aluminate.aluminate_global_backend.model;

import jakarta.persistence.*;
import lombok.*;

import java.math.BigDecimal;
import java.util.List;

@Entity
@Builder
@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
public class SubscriptionPlan {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    private BigDecimal price;

    private Integer memberLimit;

    private String name;

    private Integer durationInMonths;

    private Integer storageInGB;

    private Integer cpu;

    private Integer ram;

    @OneToMany(mappedBy = "subscriptionPlan" , cascade = CascadeType.ALL, orphanRemoval = true)
    private List<SubscriptionPlanFeature> subscriptionPlanFeatures;
    
    public boolean isPresent() {
        return id != null;
    }
}
