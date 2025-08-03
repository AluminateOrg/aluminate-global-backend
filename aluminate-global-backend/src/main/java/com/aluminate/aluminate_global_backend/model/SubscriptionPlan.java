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

    @ElementCollection
    @CollectionTable(
            name = "subscription_plan_features",
            joinColumns = @JoinColumn(name = "subscription_plan_id")
    )
    @Column(name = "feature")
    private List<String> features;



    public boolean isPresent() {
        return id != null;
    }
}
