package com.aluminate.aluminate_global_backend.dto.syncOrgTickets;


import lombok.Data;

@Data
public class OrgTicketdto {
    private Double amount;
    private String status;
    private Long organizationId;
    private String key;
}
