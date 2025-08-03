package com.aluminate.aluminate_global_backend.controller;


import com.aluminate.aluminate_global_backend.config.ResponseWrapper;
import com.aluminate.aluminate_global_backend.dto.getInfo.InfoResponse;
import com.aluminate.aluminate_global_backend.dto.payment.HashRequest;
import com.aluminate.aluminate_global_backend.dto.payment.HashResponse;
import com.aluminate.aluminate_global_backend.model.Admin;
import com.aluminate.aluminate_global_backend.model.Organization;
import com.aluminate.aluminate_global_backend.service.payment.HashService;
import jakarta.validation.Valid;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequestMapping("${api.prefix}/admin/payment")
public class PaymentController {
    private final HashService hashService;
    private final Logger logger = LoggerFactory.getLogger(PaymentController.class);


    @Autowired
    public PaymentController(HashService hashService) {
        logger.info("Initializing PaymentController");
        this.hashService = hashService;
    }

    @PostMapping("/generate-hash")
    public ResponseEntity<ResponseWrapper<HashResponse>> generateHash(@Valid @RequestBody HashRequest request) {
        logger.info("Generating hash for payment request: {}", request);
        //get the current admin and organization from the security context
        Admin admin = (Admin) SecurityContextHolder.getContext().getAuthentication().getPrincipal();
        Organization organization = admin.getOrganization();
        HashResponse hashResponse = hashService.generateHash(request.getAmount(), request.getCurrency(), organization, admin);

        ResponseWrapper<HashResponse> body = new ResponseWrapper<>(
                true,
                "Hash generation successful",
                hashResponse
        );
        return ResponseEntity.ok(body);

    }


}
