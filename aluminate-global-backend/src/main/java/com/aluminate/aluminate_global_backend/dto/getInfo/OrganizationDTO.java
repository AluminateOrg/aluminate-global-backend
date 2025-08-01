package com.aluminate.aluminate_global_backend.dto.getInfo;

import com.aluminate.aluminate_global_backend.model.Status;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.time.LocalDate;
import java.time.LocalDateTime;

@Data
@NoArgsConstructor
@AllArgsConstructor
public class OrganizationDTO {
    private Long id;
    private String organizationName;
    private String subscriptionPlan;
    private LocalDateTime createdAt;
    private LocalDate nextRenewalDate;
    private String subdomain;
    private String portalUrl;
    private int maxMemberCount;
    private int currentMemberCount;
    private Status status;
}
