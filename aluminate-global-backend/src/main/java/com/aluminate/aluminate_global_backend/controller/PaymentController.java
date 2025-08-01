package com.aluminate.aluminate_global_backend.controller;


import com.aluminate.aluminate_global_backend.dto.payment.HashRequest;
import com.aluminate.aluminate_global_backend.dto.payment.HashResponse;
import com.aluminate.aluminate_global_backend.model.Admin;
import com.aluminate.aluminate_global_backend.model.Organization;
import com.aluminate.aluminate_global_backend.service.payment.HashService;
import jakarta.validation.Valid;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequestMapping("${api.prefix}/payment")
public class PaymentController {
    private final HashService hashService;


    @Autowired
    public PaymentController(HashService hashService) {
        this.hashService = hashService;
    }

//    @PostMapping("/generate-hash")
//    public ResponseEntity<HashResponse> generateHash(@Valid @RequestBody HashRequest request) {
//        //get the organization and admin from the security
//        Organization org = SecurityContextHolder.getContext().getAuthentication().getPrincipal();
//        Admin admin = SecurityContextHolder.getContext().getAuthentication().getDetails();
//
//        if (org == null || admin == null) {
//            return ResponseEntity.badRequest().body(new HashResponse("Organization or Admin not found"));
//        }
//
//
//        HashResponse hashResponse = hashService.generateHash(request.getAmount(), request.getCurrency(), org,admin);
//
//        return ResponseEntity.ok(hashResponse);
//    }


}
