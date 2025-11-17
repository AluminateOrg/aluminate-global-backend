package com.aluminate.aluminate_global_backend.dto.syncOrgTickets;

import com.aluminate.aluminate_global_backend.model.Organization;
import com.aluminate.aluminate_global_backend.model.TransactionStatus;
import jakarta.persistence.*;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.math.BigDecimal;
import java.time.LocalDateTime;

@Data
@AllArgsConstructor
@NoArgsConstructor
@Builder
public class OrgTicketResponseDTO {

    private Long id;

    private BigDecimal amount;

    @Enumerated(EnumType.STRING)
    @Builder.Default
    private TransactionStatus status = TransactionStatus.PENDING;

    private long organizationId;

    private String organizationName;

    private LocalDateTime issuedDate;

}
