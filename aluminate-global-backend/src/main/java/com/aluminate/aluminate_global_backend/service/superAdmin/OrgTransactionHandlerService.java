package com.aluminate.aluminate_global_backend.service.superAdmin;

import com.aluminate.aluminate_global_backend.config.ResponseWrapper;
import com.aluminate.aluminate_global_backend.config.util.RSAEncryptionUtil;
import com.aluminate.aluminate_global_backend.dto.syncOrgTickets.OrgMainTicketAck;
import com.aluminate.aluminate_global_backend.dto.syncOrgTickets.OrgTicketProjection;
import com.aluminate.aluminate_global_backend.dto.syncOrgTickets.OrgTicketResponseDTO;
import com.aluminate.aluminate_global_backend.model.OrgPayout;
import com.aluminate.aluminate_global_backend.model.OrgTicket;
import com.aluminate.aluminate_global_backend.model.PaymentCategory;
import com.aluminate.aluminate_global_backend.model.TransactionStatus;
import com.aluminate.aluminate_global_backend.repository.OrgPayoutRepository;
import com.aluminate.aluminate_global_backend.repository.OrgTicketRepository;
import com.aluminate.aluminate_global_backend.service.orgClient.OrgRequestService;
import jakarta.annotation.PostConstruct;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Service;

import java.security.PublicKey;
import java.time.LocalDateTime;

@Service
public class OrgTransactionHandlerService {
    //general organization transaction handling methods will be defined here
    private OrgTicketRepository  orgTicketRepository;
    private OrgPayoutRepository orgPayoutRepository;
    private final Logger logger = LoggerFactory.getLogger(this.getClass());
    @Value("${encryption.organization.public-key}")
    private String organizationPublicKeyENV;
    private PublicKey organizationPublicKey;
    private final OrgRequestService orgRequestService;


    public OrgTransactionHandlerService(
            OrgTicketRepository orgTicketRepository,
            OrgPayoutRepository orgPayoutRepository,
            OrgRequestService orgRequestService

    ) {
        this.orgTicketRepository = orgTicketRepository;
        this.orgPayoutRepository = orgPayoutRepository;
        this.orgRequestService = orgRequestService;
    }

    @PostConstruct
    public void initKeys() throws Exception {
        this.organizationPublicKey = RSAEncryptionUtil.publicKeyFromPem(organizationPublicKeyENV);
    }
    public Page<OrgTicketResponseDTO> getTickets(int offset,
                                      int limit,
                                      String search,
                                      TransactionStatus status,
                                      String from,
                                      String to) {

        try {
            logger.info("getTickets with offset: {}, limit: {}", offset, limit);
            Pageable pageable = PageRequest.of(offset / limit, limit);

            LocalDateTime fromDate = null;
            LocalDateTime toDate = null;

            //parse from and to LocalDateTime
            if (from != null && !from.isBlank()) {
                fromDate = LocalDateTime.parse(from);  // handles "2025-11-16T00:00:00"
            }

            if(fromDate == null) {
                fromDate = LocalDateTime.of(1970,1,1,0,0);
            }
            if(toDate == null) {
                toDate = LocalDateTime.now();
            }

            if (to != null && !to.isBlank()) {
                toDate = LocalDateTime.parse(to);
            }

            if(search == null || search.isEmpty()) {
                search = "";
            } else {
                search = search.trim();

            }


            logger.info("Searching tickets with search: {}, status: {}, from: {}, to: {}", search, status, fromDate, toDate);
            Page<OrgTicketProjection> orgTickets = orgTicketRepository.findTickets(search, status, fromDate, toDate, pageable);

            //convert OrgTicketProjection to OrgTicketResponseDTO
            Page<OrgTicketResponseDTO> dtoPage = orgTickets.map(projection -> new OrgTicketResponseDTO(
                    projection.getId(),
                    projection.getAmount(),
                    projection.getStatus(),
                    projection.getOrganizationId(),
                    projection.getOrganizationName(),
                    projection.getCreatedAt()
            ));



            logger.info("Fetched {} tickets", orgTickets.getTotalElements());
            return dtoPage;

        } catch (Exception ex) {
            throw new RuntimeException("Failed to fetch tickets: " + ex.getMessage(), ex);
        }
    }

    public void markTicketAsPaid(Long ticketId) {
        try {
            logger.info("markTicketAsPaid with ticketId: {}", ticketId);
            OrgTicket ticket = orgTicketRepository.findById(ticketId)
                    .orElseThrow(() -> new RuntimeException("Ticket not found with ID: " + ticketId));


            ticket.setStatus(TransactionStatus.PAID);
            orgTicketRepository.save(ticket);

            //create org payout
            OrgPayout orgPayout = OrgPayout.builder()
                    .orgTicket(ticket)
                    .amount(ticket.getAmount())
                    .paymentCategory(PaymentCategory.ORGANIZATION_TICKET)
                    .build();

            //save
            orgPayoutRepository.save(orgPayout);
            //send to server
            Boolean isSent = sendTicketToOrgServer(ticket, TransactionStatus.PAID);
            if(!isSent) {
                throw new RuntimeException("Failed to send ticket to organization server");
            }


            logger.info("Ticket with ID: {} marked as PAID & saved in org payouts", ticketId);
        } catch (Exception ex) {
            throw new RuntimeException("Failed to mark ticket as PAID: " + ex.getMessage(), ex);
        }
    }

    public void rejectTicket(Long ticketId) {
        try {
            logger.info("rejectTicket with ticketId: {}", ticketId);
            OrgTicket ticket = orgTicketRepository.findById(ticketId)
                    .orElseThrow(() -> new RuntimeException("Ticket not found with ID: " + ticketId));
            ticket.setStatus(TransactionStatus.REJECTED);
            orgTicketRepository.save(ticket);

            //send to server
            Boolean isSent = sendTicketToOrgServer(ticket, TransactionStatus.REJECTED);
            if(!isSent) {
                throw new RuntimeException("Failed to send ticket to organization server");
            }

            logger.info("Ticket with ID: {} marked as REJECTED", ticketId);
        } catch (Exception ex) {
            throw new RuntimeException("Failed to reject ticket: " + ex.getMessage(), ex);
        }
    }

    //function to send ticket to org server
    public Boolean sendTicketToOrgServer(OrgTicket ticket,TransactionStatus status) {
        try{
            //encrypt ticket key
            String encryptedTicketKey = RSAEncryptionUtil.encrypt(
                    ticket.getKey(),
                    organizationPublicKey
            );

            //build OrgMainTicketAck
            OrgMainTicketAck orgMainTicketAck = OrgMainTicketAck.builder()
                    .encryptedTicketKey(encryptedTicketKey)
                    .amount(ticket.getAmount())
                    .organizationId(ticket.getOrganization().getId())
                    .transactionStatus(status)
                    .build();

            //send ticket to org server
            return orgRequestService.send(
                   ticket.getOrganization().getId(),
                   orgMainTicketAck
           );
        } catch (Exception e) {
            throw new RuntimeException(e);
        }

    }
}
