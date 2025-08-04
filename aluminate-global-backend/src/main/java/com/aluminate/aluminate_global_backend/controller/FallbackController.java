package com.aluminate.aluminate_global_backend.controller;

import com.aluminate.aluminate_global_backend.dto.TestRequest;
import jakarta.servlet.http.HttpServletRequest;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.http.ResponseEntity;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("${api.prefix}/public/fallback")
public class FallbackController {
    private final Logger log = LoggerFactory.getLogger(FallbackController.class);
    private final PasswordEncoder passwordEncoder;

    public FallbackController(PasswordEncoder passwordEncoder) {
        this.passwordEncoder = passwordEncoder;
    }

    @PostMapping("/sayHello")
    public ResponseEntity<String> sayHello(@RequestBody TestRequest request) {
        log.info("FallbackController: Received request to /sayHello with message: {}", request.getMessage());

        return ResponseEntity.ok("password->" + passwordEncoder.encode(request.getMessage()));

    }
}

