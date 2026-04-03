package com.aluminate.aluminate_global_backend.dto.payment;

import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
public class PaymentUpdateRequest {
    private Long subscriptionPlanId;
    private Long orgId;
}
