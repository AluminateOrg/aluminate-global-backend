package com.aluminate.aluminate_global_backend.dto.payment;

import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
public class HashRequest {
    @NotNull(message = "Amount is required")
    private Double amount;
    private String currency;
}
