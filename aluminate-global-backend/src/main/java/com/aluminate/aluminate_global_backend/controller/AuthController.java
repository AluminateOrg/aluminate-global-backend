package com.aluminate.aluminate_global_backend.controller;


import com.aluminate.aluminate_global_backend.config.ResponseWrapper;
import com.aluminate.aluminate_global_backend.config.util.RSAEncryptionUtil;
import com.aluminate.aluminate_global_backend.dto.getInfo.LogInfoResponse;
import com.aluminate.aluminate_global_backend.dto.login.EncryptedLoginRequest;
import com.aluminate.aluminate_global_backend.dto.login.LoginRequest;
import com.aluminate.aluminate_global_backend.dto.org.GlobalAuthResponse;
import com.aluminate.aluminate_global_backend.dto.otp.OtpRequest;
import com.aluminate.aluminate_global_backend.dto.registration.DecryptedCredentials;
import com.aluminate.aluminate_global_backend.dto.registration.EncryptedRegistrationRequest;
import com.aluminate.aluminate_global_backend.dto.registration.FinalRegistrationRequest;
import com.aluminate.aluminate_global_backend.dto.registration.RegistrationRequest;
import com.aluminate.aluminate_global_backend.service.auth.AuthService;
import com.aluminate.aluminate_global_backend.service.csrf.CsrfTokenService;
import com.aluminate.aluminate_global_backend.service.email.EmailService;
import com.aluminate.aluminate_global_backend.service.otp.OtpService;
import com.fasterxml.jackson.databind.ObjectMapper;
import jakarta.annotation.PostConstruct;
import jakarta.mail.MessagingException;
import jakarta.servlet.http.HttpServletResponse;
import jakarta.validation.Valid;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.http.*;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.reactive.function.client.WebClient;

import java.io.IOException;
import java.security.PrivateKey;
import java.security.PublicKey;
import java.util.Optional;
import java.util.Random;
import java.util.logging.Logger;

@RestController
@RequestMapping("${api.prefix}/auth")
public class AuthController {
    // This controller will handle authentication-related endpoints.
    // Currently, it does not have any methods defined.
    // You can add methods for login, registration, etc. as needed.
    @Value("${encryption.global.private-key}")
    private String globalPrivateKeyENV;

    @Value("${encryption.organization.public-key}")
    private String organizationPublicKeyENV;


    private final ObjectMapper objectMapper = new ObjectMapper();
    private final AuthService authService;
    private final EmailService emailService;
    private final OtpService otpService;
    private final CsrfTokenService csrfTokenService;
    private static final Logger logger = Logger.getLogger(AuthController.class.getName());
    private PrivateKey globalPrivateKey;
    private PublicKey organizationPublicKey;



    @Autowired
    private WebClient.Builder webClientBuilder;




    public AuthController(AuthService authService, CsrfTokenService csrfTokenService, EmailService emailService, OtpService otpService) {
        this.emailService = emailService;
        this.authService = authService;
        this.csrfTokenService = csrfTokenService;
        this.otpService = otpService;

    }
    @PostConstruct
    public void initKeys() throws Exception {
        this.globalPrivateKey = RSAEncryptionUtil.privateKeyFromPem(globalPrivateKeyENV);
        this.organizationPublicKey = RSAEncryptionUtil.publicKeyFromPem(organizationPublicKeyENV);
    }


    @PostMapping("/register")
    public ResponseEntity<ResponseWrapper<String>> register(@Valid @RequestBody EncryptedRegistrationRequest EncryptedRequest, HttpServletResponse httpResponse) {
        try{
            logger.info("Reached Auth Controller! request-> " + EncryptedRequest);
            // Decrypt the registration request
            String decrypted = RSAEncryptionUtil.decrypt(
                    EncryptedRequest.getPayload(),
                    globalPrivateKey
            );
            logger.info("decrypted: " + decrypted);
            DecryptedCredentials credentials = objectMapper.readValue(
                    decrypted,
                    DecryptedCredentials.class
            );

            // send otp to the email and verify


            FinalRegistrationRequest request = new FinalRegistrationRequest(
                    EncryptedRequest.getObj().getOrganizationName(),
                    EncryptedRequest.getObj().getAdminFullName(),
                    credentials.getEmail(),
                    EncryptedRequest.getObj().getPhoneNumber(),
                    credentials.getPassword(),
                    EncryptedRequest.getObj().getNationalId()
            );

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
    public ResponseEntity<ResponseWrapper<String>> login(@Valid @RequestBody EncryptedLoginRequest encryptedLoginRequest, HttpServletResponse httpResponse) {
        try {
            logger.info("Reached Auth Controller!");
            //decrypt

            String decrypted = RSAEncryptionUtil.decrypt(
                    encryptedLoginRequest.getPayload(),
                    globalPrivateKey
            );
            LoginRequest loginRequest = objectMapper.readValue(
                    decrypted,
                    LoginRequest.class
            );
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

    @PostMapping("/verify-admin")
    public ResponseEntity<GlobalAuthResponse> verifyAdminCredentials(@Valid @RequestBody String encryptedLoginRequest) {
        try {
            logger.info("Reached Verify Admin Credentials Controller!");
            String decrypted = RSAEncryptionUtil.decrypt(
                    encryptedLoginRequest,
                    globalPrivateKey
            );
            logger.info("decrypted: " + decrypted);
            LoginRequest loginRequest = objectMapper.readValue(
                    decrypted,
                    LoginRequest.class
            );
            GlobalAuthResponse response = authService.verifyAdminCredentials(loginRequest);
            logger.info("Admin credentials verified successfully!");




            GlobalAuthResponse body = new GlobalAuthResponse(true, response.getAdmin(),response.getOrganization());
            logger.info("Returning response: " + body);
            return ResponseEntity.ok(body);
        } catch (Exception e) {
            logger.warning("Error verifying admin credentials: " + e.getMessage());

            throw new RuntimeException(e);
        }
    }

    @GetMapping("/send-otp")
    public ResponseEntity<ResponseWrapper<String>> sendOtp(@RequestParam String email) throws MessagingException, IOException {
        try{
            String otp = String.format("%06d", new Random().nextInt(999999));
            logger.info("Generated OTP: " + otp + " for email: " + email);
            emailService.sendOtpMail(email, otp);
            logger.info("OTP email sent to: " + email);
            otpService.saveOtp(email, otp);
            ResponseWrapper<String> body = new ResponseWrapper<>(true, "OTP sent successfully", otp);
            return ResponseEntity.ok(body);
        } catch (Exception e){
            logger.warning("Error sending OTP to " + email + ": " + e.getMessage());
            ResponseWrapper<String> body = new ResponseWrapper<>(false, "Failed to send OTP", null);
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).body(body);
        }

    }

    @PostMapping("/verify-otp")
    public ResponseEntity<ResponseWrapper<String>> verifyOtp(@RequestBody OtpRequest otpRequest) {
        System.out.println("email and otp" + otpRequest.getEmail() + " " + otpRequest.getOtp() );
        boolean isValid = otpService.verifyOtp(otpRequest.getEmail(), otpRequest.getOtp());
        if (isValid) {
            ResponseWrapper<String> body = new ResponseWrapper<>(true, "OTP verified successfully", null);
            return ResponseEntity.ok(body);
        } else {
            ResponseWrapper<String> body = new ResponseWrapper<>(false, "Invalid OTP", null);
            return ResponseEntity.status(HttpStatus.BAD_REQUEST).body(body);
        }
    }


}
