package com.aluminate.aluminate_global_backend.dto.syncOrgTickets;

import com.aluminate.aluminate_global_backend.model.TransactionStatus;

import java.math.BigDecimal;
import java.time.LocalDateTime;

public interface OrgTicketProjection {
    Long getId();
    BigDecimal getAmount();
    TransactionStatus getStatus();
    Long getOrganizationId();
    String getOrganizationName();
    LocalDateTime getCreatedAt();
}
