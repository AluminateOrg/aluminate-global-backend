package com.aluminate.aluminate_global_backend.dto.syncOrgTickets;

import lombok.Data;

@Data
public class DecryptedTicket {
    //encrypted ticket key & amount
    private String keyAndAmountEncrypted;

    //rest
    private Long organizationId;
}
