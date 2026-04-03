package com.aluminate.aluminate_global_backend.dto.syncOrgTickets;


import com.aluminate.aluminate_global_backend.model.TransactionStatus;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.math.BigDecimal;

@Data
@AllArgsConstructor
@NoArgsConstructor
@Builder
public class OrgMainTicketAck {
    private String encryptedTicketKey;
    private BigDecimal amount;
    private Long organizationId;
    private TransactionStatus transactionStatus;
}
