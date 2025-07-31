package com.aluminate.aluminate_global_backend.model;

import jakarta.persistence.*;
import lombok.*;

import java.math.BigDecimal;
import java.time.LocalDateTime;

@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@Entity
@Builder
public class Transaction {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id; // This will act as order_id for PayHere

    @Column(nullable = false)
    private BigDecimal amount;

    @Column(nullable = false)
    private String currency;

    private String paymentId;         // From PayHere
    private String method;            // e.g. VISA, EZCash
    private String statusCode;        // e.g. 2 for success
    private String statusMessage;     // Human-readable message from PayHere
    private String cardHolderName;
    private String cardNo;
    private String cardExpiry;

    @Enumerated(EnumType.STRING)
    private TransactionStatus transactionStatus;

    private String custom1; // Optional — can be used to store portal info, referral, etc.
    private String custom2;

    @Builder.Default
    private LocalDateTime createdAt = LocalDateTime.now();

    // Who made the transaction
    @ManyToOne
    @JoinColumn(name = "organization_id", nullable = false)
    private Organization organization;

    @ManyToOne
    @JoinColumn(name = "admin_id")
    private Admin admin;
}
