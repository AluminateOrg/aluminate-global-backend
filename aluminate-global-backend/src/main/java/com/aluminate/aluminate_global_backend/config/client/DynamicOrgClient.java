package com.aluminate.aluminate_global_backend.config.client;

import com.aluminate.aluminate_global_backend.config.ResponseWrapper;
import com.aluminate.aluminate_global_backend.dto.syncOrgTickets.OrgMainTicketAck;
import org.springframework.cloud.openfeign.FeignClient;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;

@FeignClient(name = "dynamic-organization-backend", url="DYNAMIC_ORG_URL")
public interface DynamicOrgClient {

    // Define your methods here, for example:
    // @PostMapping("/some-endpoint")
    // ResponseEntity<SomeResponseType> sendToOrg(@RequestBody SomeRequestType payload);

    @PostMapping("/public/transactionTicketHandler/handleTicket")
    ResponseWrapper<Boolean> sendTicketAckToOrg(
            @RequestBody OrgMainTicketAck orgMainTicketAck
            );
}
