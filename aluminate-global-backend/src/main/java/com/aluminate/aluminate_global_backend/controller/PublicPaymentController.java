package com.aluminate.aluminate_global_backend.controller;


import com.aluminate.aluminate_global_backend.config.ResponseWrapper;
import com.aluminate.aluminate_global_backend.dto.payment.PaymentNotifyRequest;
import com.aluminate.aluminate_global_backend.dto.payment.PaymentUpdateRequest;
import com.aluminate.aluminate_global_backend.service.organization.OrgService;
import com.aluminate.aluminate_global_backend.service.payment.PaymentNotifyService;
import jakarta.validation.Valid;
import org.aspectj.weaver.ast.Or;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("${api.prefix}/public/payment")
public class PublicPaymentController {

    private final PaymentNotifyService paymentNotifyService;
    private final Logger logger = LoggerFactory.getLogger(PublicPaymentController.class);
    private final OrgService orgService;

    public PublicPaymentController(PaymentNotifyService paymentNotifyService,
                                   OrgService orgService) {
        this.paymentNotifyService = paymentNotifyService;
        this.orgService = orgService;
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

    @PostMapping("/update/org")
    public ResponseEntity<ResponseWrapper<Boolean>> updateOrg(@Valid @RequestBody PaymentUpdateRequest request) {
        try {
            boolean isUpdated = orgService.updateOrganization(request.getSubscriptionPlanId(), request.getOrgId());
            if (isUpdated) {
                return ResponseEntity.ok(new ResponseWrapper<>(true, "Payment updated successfully", true));
            } else {
                return ResponseEntity.ok(new ResponseWrapper<>(false, "Payment updated failed", false));
            }
        } catch (Exception e) {
            throw new RuntimeException(e);
        }
    }

}
