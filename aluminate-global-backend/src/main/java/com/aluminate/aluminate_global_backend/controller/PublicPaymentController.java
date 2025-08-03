package com.aluminate.aluminate_global_backend.controller;


import com.aluminate.aluminate_global_backend.config.ResponseWrapper;
import com.aluminate.aluminate_global_backend.dto.payment.PaymentNotifyRequest;
import com.aluminate.aluminate_global_backend.service.payment.PaymentNotifyService;
import jakarta.validation.Valid;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("${api.prefix}/public/payment")
public class PublicPaymentController {

    private final PaymentNotifyService paymentNotifyService;
    private final Logger logger = LoggerFactory.getLogger(PublicPaymentController.class);

    public PublicPaymentController(PaymentNotifyService paymentNotifyService) {
        this.paymentNotifyService = paymentNotifyService;
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

}
