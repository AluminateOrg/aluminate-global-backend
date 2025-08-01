package com.aluminate.aluminate_global_backend.controller;


import com.aluminate.aluminate_global_backend.config.ResponseWrapper;
import com.aluminate.aluminate_global_backend.dto.getInfo.InfoResponse;
import com.aluminate.aluminate_global_backend.dto.registration.RegistrationRequest;
import com.aluminate.aluminate_global_backend.service.auth.AuthService;
import com.aluminate.aluminate_global_backend.service.csrf.CsrfTokenService;
import jakarta.servlet.http.HttpServletResponse;
import jakarta.validation.Valid;
import org.springframework.http.ResponseCookie;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.time.Duration;
import java.util.UUID;
import java.util.logging.Logger;

@RestController
@RequestMapping("${api.prefix}/auth")
public class AuthController {
    // This controller will handle authentication-related endpoints.
    // Currently, it does not have any methods defined.
    // You can add methods for login, registration, etc. as needed.

    private final AuthService authService;
    private final CsrfTokenService csrfTokenService;
    private static final Logger logger = Logger.getLogger(AuthController.class.getName());

    public AuthController(AuthService authService, CsrfTokenService csrfTokenService) {
        this.authService = authService;
        this.csrfTokenService = csrfTokenService;
    }


    @PostMapping("/register")
    public ResponseEntity<ResponseWrapper<String>> register(
            @Valid @RequestBody RegistrationRequest request,
            HttpServletResponse httpResponse

    ) {
        try{
            logger.info("Reached Auth Controller!");
            String token = authService.register(request);

            String sessionId = UUID.randomUUID().toString();

            String csrfToken = csrfTokenService.generateAndStoreToken(sessionId);



            // Set JWT as HTTP-only cookie
            ResponseCookie cookie = ResponseCookie.from("jwt", token)
                    .httpOnly(true)
                    .secure(false) // set to false in dev if needed
                    .sameSite("Strict")
                    .path("/")
                    .maxAge(Duration.ofDays(1))
                    .build();
            ResponseCookie csrfCookie = ResponseCookie.from("csrf-token", csrfToken)
                    .httpOnly(false) // Client-side JS must read this
                    .secure(false)
                    .sameSite("Strict")
                    .path("/")
                    .maxAge(Duration.ofDays(1))
                    .build();
            ResponseCookie sessionCookie = ResponseCookie.from("sessionId", sessionId)
                    .httpOnly(false)
                    .secure(false)
                    .sameSite("Strict")
                    .path("/")
                    .maxAge(Duration.ofDays(1))
                    .build();

            httpResponse.addHeader("Set-Cookie", cookie.toString());
            httpResponse.addHeader("Set-Cookie", csrfCookie.toString());
            httpResponse.addHeader("Set-Cookie", sessionCookie.toString());
            logger.info("user registered!");
            // You can return null or some data
            ResponseWrapper<String> body = new ResponseWrapper<>(true, "Registration successful", null);
            return ResponseEntity.ok(body);
        }catch (Exception e){
            throw new RuntimeException(e);

        }



    }








}
