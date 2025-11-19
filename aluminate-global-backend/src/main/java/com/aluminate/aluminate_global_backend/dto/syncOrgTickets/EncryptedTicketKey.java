package com.aluminate.aluminate_global_backend.dto.syncOrgTickets;

import lombok.Data;

import java.math.BigDecimal;

@Data
public class EncryptedTicketKey {
   //encrypted ticket data
    private String encryptedTicketKey;
    private BigDecimal amount;
    //rest
    private Long organizationId;
}
