package com.aluminate.aluminate_global_backend.controller.superAdmin;

import com.aluminate.aluminate_global_backend.config.ResponseWrapper;
import com.aluminate.aluminate_global_backend.dto.syncOrgTickets.OrgTicketResponseDTO;
import com.aluminate.aluminate_global_backend.dto.syncOrgTickets.TicketPageResponse;
import com.aluminate.aluminate_global_backend.model.OrgTicket;
import com.aluminate.aluminate_global_backend.model.TransactionStatus;
import com.aluminate.aluminate_global_backend.service.superAdmin.OrgTransactionHandlerService;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.PageRequest;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.http.ResponseEntity;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.web.bind.annotation.*;


@RestController
@RequestMapping("${api.prefix}/superAdmin/orgTransaction")
public class OrgTransactionHandler {
    private final Logger logger = LoggerFactory.getLogger(this.getClass());
    private final OrgTransactionHandlerService ticketService;

    public OrgTransactionHandler(OrgTransactionHandlerService ticketService) {
        this.ticketService = ticketService;
    }
    //general organization transaction handling endpoints will be defined here

    @GetMapping("/getTransactionTickets")
    public ResponseEntity<ResponseWrapper<TicketPageResponse>> getTransactionTickets(
            @RequestParam int offset,
            @RequestParam int limit,
            @RequestParam(required = false) String search,
            @RequestParam(required = false) String status,
            @RequestParam(required = false) String from,
            @RequestParam(required = false) String to
    ) {
        try{
            logger.info("Fetching organization transaction tickets with offset: {}, limit: {}, search: {}, status: {}, from: {}, to: {}",
                    offset, limit, search, status, from, to);

            // Convert status string to enum
            TransactionStatus transactionStatus = null;
            if (status != null) {
                try {
                    transactionStatus = TransactionStatus.valueOf(status.toUpperCase());
                } catch (IllegalArgumentException e) {
                    logger.error("Invalid transaction status: {}", status);
                    return ResponseEntity.badRequest().body(
                            new ResponseWrapper<>(
                                    false,
                                    "Invalid transaction status: " + status,
                                    null
                            )
                    );
                }
            }



            // Call service to get tickets (Page<OrgTicket>)
            Page<OrgTicketResponseDTO> page = ticketService.getTickets(offset, limit, search, transactionStatus, from, to);
            // Convert Page -> DTO
            TicketPageResponse response = new TicketPageResponse(page.getContent(), page.getTotalElements());
            logger.info("Returning tickets for offset: {}, limit: {}", offset, limit);
            // Wrap DTO in ResponseWrapper
            return ResponseEntity.ok(
                    new ResponseWrapper<>(true, "Fetched organization transaction tickets successfully", response)
            );

        } catch (Exception e) {
            logger.error(e.getMessage(), e);
            throw new RuntimeException(e);
        }


    }

    @PutMapping("/markAsPaid")
    @Transactional
    public ResponseEntity<ResponseWrapper<Boolean>> markTicketAsPaid(@RequestParam Long ticketId) {
        try {
            logger.info("Marking ticket as PAID with ID: {}", ticketId);
            ticketService.markTicketAsPaid(ticketId);
            return ResponseEntity.ok(
                    new ResponseWrapper<>(true, "Ticket marked as PAID successfully", true)
            );
        } catch (Exception e) {
            logger.error(e.getMessage(), e);
            throw new RuntimeException(e);
        }
    }

    @PutMapping("/reject")
    @Transactional
    public ResponseEntity<ResponseWrapper<Boolean>> rejectTicket(@RequestParam Long ticketId) {
        try {
            logger.info("Rejecting ticket with ID: {}", ticketId);
            ticketService.rejectTicket(ticketId);
            return ResponseEntity.ok(
                    new ResponseWrapper<>(true, "Ticket rejected successfully", true)
            );
        } catch (Exception e) {
            logger.error(e.getMessage(), e);
            throw new RuntimeException(e);
        }
    }




}
