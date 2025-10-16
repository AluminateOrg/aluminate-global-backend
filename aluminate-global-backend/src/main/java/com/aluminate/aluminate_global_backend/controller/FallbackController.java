package com.aluminate.aluminate_global_backend.controller;

import com.aluminate.aluminate_global_backend.config.util.RSAEncryptionUtil;
import com.aluminate.aluminate_global_backend.dto.TestRequest;
import jakarta.annotation.PostConstruct;
import jakarta.servlet.http.HttpServletRequest;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.http.ResponseEntity;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.web.bind.annotation.*;

import java.security.PrivateKey;

@RestController
@RequestMapping("${api.prefix}/public/fallback")
public class FallbackController {
    private final Logger log = LoggerFactory.getLogger(FallbackController.class);
    private final PasswordEncoder passwordEncoder;
    @Value("${encryption.global.private-key}")
    private String globalPrivateKeyENV;

    private PrivateKey globalPrivateKey;


    public FallbackController(PasswordEncoder passwordEncoder) {
        this.passwordEncoder = passwordEncoder;
    }
    @PostConstruct
    public void initKeys() throws Exception {
        this.globalPrivateKey = RSAEncryptionUtil.privateKeyFromPem(globalPrivateKeyENV);
    }

    @PostMapping("/sayHello")
    public ResponseEntity<String> sayHello(@RequestBody TestRequest request) {
        log.info("FallbackController: Received request to /sayHello with message: {}", request.getMessage());

        return ResponseEntity.ok("password->" + passwordEncoder.encode(request.getMessage()));

    }
    @PostMapping("/testEncryption")
    public ResponseEntity<String> testEncryption(@RequestBody TestRequest request, HttpServletRequest httpServletRequest) {
        log.info("FallbackController: Received request to /testEncryption with message: {}", request);
        try {

            String decryptedMessage = RSAEncryptionUtil.decrypt(request.getMessage(), globalPrivateKey);
            log.info("Decrypted message: {}", decryptedMessage);
            return ResponseEntity.ok("Decrypted message: " + decryptedMessage);
        } catch (Exception e) {
            log.error("Error during decryption", e);
            return ResponseEntity.status(500).body("Decryption error: " + e.getMessage());
        }

    }
}

