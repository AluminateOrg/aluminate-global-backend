package com.aluminate.aluminate_global_backend.model;

import jakarta.persistence.*;
import lombok.*;

import java.math.BigDecimal;
import java.time.LocalDateTime;

@Entity
@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class OrgPayout {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @ManyToOne
    @JoinColumn(name = "org_ticket_id", nullable = false, foreignKey = @ForeignKey(name = "FK_ORG_PAYOUTS_ORG_TICKET",
            foreignKeyDefinition = "FOREIGN KEY (org_ticket_id) REFERENCES org_ticket(id) ON UPDATE CASCADE ON DELETE SET NULL"))
    private OrgTicket orgTicket;

    private BigDecimal amount;

    @Builder.Default
    private LocalDateTime createdAt = LocalDateTime.now();

    @Enumerated(EnumType.STRING)
    private PaymentCategory paymentCategory;



}
