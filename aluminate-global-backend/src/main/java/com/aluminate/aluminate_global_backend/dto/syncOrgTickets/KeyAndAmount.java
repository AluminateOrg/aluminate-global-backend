package com.aluminate.aluminate_global_backend.dto.syncOrgTickets;

import lombok.*;

import java.math.BigDecimal;

@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
@Getter
public class KeyAndAmount {
    private String key;
    private BigDecimal amount;
}
