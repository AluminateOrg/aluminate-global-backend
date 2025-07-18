package com.aluminate.aluminate_global_backend.model;

import jakarta.persistence.*;
import lombok.*;

import java.time.LocalDate;
import java.time.LocalDateTime;

@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@Entity
@Builder

public class Organization {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Column(nullable = false)
    private String organizationName;

    @Column(nullable = false)
    private String subscriptionPlan;

    @Builder.Default
    private LocalDateTime createdAt = LocalDateTime.now();

    private LocalDate nextRenewalDate;
    private String subdomain;
    private String portalUrl;
    private int maxMemberCount;
    private int currentMemberCount;

    @Enumerated(EnumType.STRING)
    private Status status;

    @Builder.Default
    private boolean isDeleted = false;

    @OneToOne
    @JoinColumn(name = "admin_id", nullable = false)
    private Admin admin;
}

