package com.aluminate.aluminate_global_backend.controller;


import com.aluminate.aluminate_global_backend.config.ResponseWrapper;
import com.aluminate.aluminate_global_backend.config.util.RSAEncryptionUtil;
import com.aluminate.aluminate_global_backend.dto.payment.PaymentNotifyRequest;
import com.aluminate.aluminate_global_backend.dto.syncOrgTickets.DecryptedTicket;
import com.aluminate.aluminate_global_backend.dto.syncOrgTickets.EncryptedTicket;
import com.aluminate.aluminate_global_backend.dto.syncOrgTickets.KeyAndAmount;
import com.aluminate.aluminate_global_backend.dto.syncOrgTickets.OrgTicketdto;
import com.aluminate.aluminate_global_backend.model.TransactionStatus;
import com.aluminate.aluminate_global_backend.service.payment.PaymentNotifyService;
import com.fasterxml.jackson.databind.ObjectMapper;
import jakarta.annotation.PostConstruct;
import jakarta.servlet.http.HttpServletResponse;
import jakarta.validation.Valid;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.security.PrivateKey;
import java.security.PublicKey;

@RestController
@RequestMapping("${api.prefix}/public/payment")
public class PublicPaymentController {

    private final PaymentNotifyService paymentNotifyService;
    private final Logger logger = LoggerFactory.getLogger(PublicPaymentController.class);
    private final ObjectMapper objectMapper;

    @Value("${encryption.global.private-key}")
    private String globalPrivateKeyENV;

    @Value("${encryption.organization.public-key}")
    private String organizationPublicKeyENV;

    private PrivateKey globalPrivateKey;
    private PublicKey organizationPublicKey;

    public PublicPaymentController(PaymentNotifyService paymentNotifyService, ObjectMapper objectMapper) {
        this.paymentNotifyService = paymentNotifyService;
        this.objectMapper = objectMapper;
    }
    @PostConstruct
    public void initKeys() throws Exception {
        this.globalPrivateKey = RSAEncryptionUtil.privateKeyFromPem(globalPrivateKeyENV);
        this.organizationPublicKey = RSAEncryptionUtil.publicKeyFromPem(organizationPublicKeyENV);
    }

    @PostMapping(value = "/notify", consumes = "application/x-www-form-urlencoded")
    public void notifyPayment(@RequestBody PaymentNotifyRequest request) {
        logger.info("Reached PublicPaymentController notifyPayment method");
        try {
            paymentNotifyService.handlePaymentNotification(request);
            logger.info("Payment notification handled successfully");

        } catch (Exception e) {
            // Log the error and return a 500 response
            logger.error("Error processing payment notification", e);
            throw new RuntimeException("Error processing payment notification", e);

        }
    }

    @GetMapping("/verify/{orderId}")
    public ResponseEntity<ResponseWrapper<Boolean>> verifyPayment(@PathVariable String orderId) {
        try {
            boolean verified = paymentNotifyService.changeOrgStatus(Long.valueOf(orderId));
            if (verified) {
                logger.info("Payment verified successfully");
                return ResponseEntity.ok(new ResponseWrapper<>(true, "Payment verified", true));
            } else {
                logger.error("Payment verification failed");
                return ResponseEntity.ok(new ResponseWrapper<>(false, "Payment verification failed", false));
            }
        } catch (Exception e) {
            logger.error("Error verifying payment", e);
            return ResponseEntity.internalServerError()
                    .body(new ResponseWrapper<>(false, "Error verifying payment", false));
        }
    }

    @PostMapping("/syncOrgTransactionTickets")
    public ResponseEntity<ResponseWrapper<Boolean>> syncOrgTransactionTickets(@RequestBody EncryptedTicket encryptedTicket, HttpServletResponse httpResponse) {
        try {
            String decrypted = RSAEncryptionUtil.decrypt(
                    encryptedTicket.getPayload(),
                    globalPrivateKey

            );
            DecryptedTicket decryptedTicket = objectMapper.readValue(decrypted, DecryptedTicket.class);

            //get decrypted key & value
            String decryptedKeyAndValue = RSAEncryptionUtil.decrypt(
                    decryptedTicket.getKeyAndAmountEncrypted(),
                    globalPrivateKey
            );

            //map object
            KeyAndAmount keyAndValue = objectMapper.readValue(decryptedKeyAndValue, KeyAndAmount.class);

            //make the OrgTicketdto
            OrgTicketdto orgTicket = new OrgTicketdto();
            orgTicket.setKey(keyAndValue.getKey());
            orgTicket.setAmount(keyAndValue.getAmount().doubleValue());
            orgTicket.setOrganizationId(decryptedTicket.getOrganizationId());
            orgTicket.setStatus(String.valueOf(TransactionStatus.PENDING));


            //call service to sync
            paymentNotifyService.syncOrgTransactionTickets(orgTicket);
            logger.info("Organization transaction tickets synchronized successfully");
            return ResponseEntity.ok(new ResponseWrapper<>(true, "Synchronization successful", true));
        } catch (Exception e) {
            logger.error("Error synchronizing organization transaction tickets", e);
            return ResponseEntity.internalServerError()
                    .body(new ResponseWrapper<>(false, "Error during synchronization", false));
        }
    }

}
