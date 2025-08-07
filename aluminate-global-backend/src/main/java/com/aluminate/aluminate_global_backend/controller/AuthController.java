package com.aluminate.aluminate_global_backend.controller;


import com.aluminate.aluminate_global_backend.config.ResponseWrapper;
import com.aluminate.aluminate_global_backend.config.exception.EmailNotVerifiedException;
import com.aluminate.aluminate_global_backend.config.exception.InactiveOrganizationException;
import com.aluminate.aluminate_global_backend.dto.getInfo.InfoResponse;
import com.aluminate.aluminate_global_backend.dto.getInfo.LogInfoResponse;
import com.aluminate.aluminate_global_backend.dto.login.LoginOrgResponse;
import com.aluminate.aluminate_global_backend.dto.login.LoginRequest;
import com.aluminate.aluminate_global_backend.dto.registration.RegistrationRequest;
import com.aluminate.aluminate_global_backend.service.auth.AuthService;
import com.aluminate.aluminate_global_backend.service.csrf.CsrfTokenService;
import jakarta.servlet.http.HttpServletResponse;
import jakarta.validation.Valid;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.*;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.reactive.function.client.ClientResponse;
import org.springframework.web.reactive.function.client.WebClient;

import java.time.Duration;
import java.util.List;
import java.util.Map;
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
    @Autowired
    private WebClient.Builder webClientBuilder;


    public AuthController(AuthService authService, CsrfTokenService csrfTokenService) {
        this.authService = authService;
        this.csrfTokenService = csrfTokenService;

    }


    @PostMapping("/register")
    public ResponseEntity<ResponseWrapper<String>> register(@Valid @RequestBody RegistrationRequest request, HttpServletResponse httpResponse) {
        try{
            logger.info("Reached Auth Controller!");
            String token = authService.register(request);

            authService.setAuthCookies(httpResponse, token);
            logger.info("user registered!");
            // You can return null or some data
            ResponseWrapper<String> body = new ResponseWrapper<>(true, "Registration successful", null);
            return ResponseEntity.ok(body);
        }catch (Exception e){
            throw new RuntimeException(e);

        }



    }

    @PostMapping("/logout")
    public ResponseEntity<ResponseWrapper<String>> logout(HttpServletResponse response) {
        // Clear cookies by setting maxAge to 0
        ResponseCookie jwtCookie = ResponseCookie.from("jwt", "")
                .httpOnly(true)
                .secure(false)
                .sameSite("Strict")
                .path("/")
                .maxAge(0)
                .build();

        ResponseCookie csrfCookie = ResponseCookie.from("csrf-token", "")
                .httpOnly(false)
                .secure(false)
                .sameSite("Strict")
                .path("/")
                .maxAge(0)
                .build();

        ResponseCookie sessionCookie = ResponseCookie.from("sessionId", "")
                .httpOnly(false)
                .secure(false)
                .sameSite("Strict")
                .path("/")
                .maxAge(0)
                .build();

        response.addHeader("Set-Cookie", jwtCookie.toString());
        response.addHeader("Set-Cookie", csrfCookie.toString());
        response.addHeader("Set-Cookie", sessionCookie.toString());

        return ResponseEntity.ok(new ResponseWrapper<>(true, "Logout successful", null));
    }

    @PostMapping("/login")
    public ResponseEntity<ResponseWrapper<String>> login(@Valid @RequestBody LoginRequest loginRequest, HttpServletResponse httpResponse) {
        try {
            logger.info("Reached Auth Controller!");
            LogInfoResponse logInfoResponse = authService.login(loginRequest);
            String token = logInfoResponse.getToken();

            authService.setAuthCookies(httpResponse, token);
            logger.info("user logged in!");
            // You can return null or some data
            ResponseWrapper<String> body = new ResponseWrapper<>(true, "Login successful", null);
            return ResponseEntity.ok(body);
        } catch (Exception e) {
            throw new RuntimeException(e);
        }
    }


    @PostMapping("/org-admin-login")
    public ResponseEntity<ResponseWrapper<LoginOrgResponse>> orgAdminLogin(@Valid @RequestBody LoginRequest loginRequest, HttpServletResponse httpResponse) {

        try{
            //check credentials and return a token claim: adminEmail
            logger.info("Reached Org Admin Login Controller!");
            LoginOrgResponse logInfoResponse = authService.orgAdminLogin(loginRequest);

            // and return the response to the org backend
            logger.info("sending Org,Admin org backend...");
            ResponseWrapper<LoginOrgResponse> body = new ResponseWrapper<>(true, "Org Admin Login successful", logInfoResponse);
            return ResponseEntity.ok(body);
        }  catch (InactiveOrganizationException e) {
            throw new RuntimeException(e);
        }

    }






}
