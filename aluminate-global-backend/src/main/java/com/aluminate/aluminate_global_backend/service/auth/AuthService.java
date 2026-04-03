package com.aluminate.aluminate_global_backend.service.auth;

import com.aluminate.aluminate_global_backend.config.exception.*;
import com.aluminate.aluminate_global_backend.config.util.Jwt;
import com.aluminate.aluminate_global_backend.dto.getInfo.*;
import com.aluminate.aluminate_global_backend.dto.login.LoginOrgResponse;
import com.aluminate.aluminate_global_backend.dto.login.LoginRequest;
import com.aluminate.aluminate_global_backend.dto.org.AdminGlobalDTO;
import com.aluminate.aluminate_global_backend.dto.org.GlobalAuthResponse;
import com.aluminate.aluminate_global_backend.dto.org.OrganizationGlobalDTO;
import com.aluminate.aluminate_global_backend.dto.registration.FinalRegistrationRequest;
import com.aluminate.aluminate_global_backend.dto.registration.RegistrationRequest;
import com.aluminate.aluminate_global_backend.model.*;
import com.aluminate.aluminate_global_backend.repository.AdminRepository;
import com.aluminate.aluminate_global_backend.repository.OrganizationRepository;
import com.aluminate.aluminate_global_backend.service.CustomUserDetailsService;
import com.aluminate.aluminate_global_backend.service.csrf.CsrfTokenService;
import jakarta.servlet.http.HttpServletResponse;
import jakarta.validation.Valid;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.http.ResponseCookie;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.Duration;
import java.util.HashMap;
import java.util.Map;
import java.util.UUID;

@Service
public class AuthService {

    private final OrganizationRepository organizationRepository;
    private final AdminRepository adminRepository;
    private final PasswordEncoder passwordEncoder;
    private final Jwt jwt;
    private final CsrfTokenService csrfTokenService;
    private final Logger logger = LoggerFactory.getLogger(AuthService.class);
    private final CustomUserDetailsService customUserDetailsService;

    public AuthService(
            OrganizationRepository organizationRepository,
            AdminRepository adminRepository,
            PasswordEncoder passwordEncoder,
            Jwt jwt,
            CsrfTokenService csrfTokenService,
            CustomUserDetailsService customUserDetailsService
    ) {
        this.organizationRepository = organizationRepository;
        this.adminRepository = adminRepository;
        this.passwordEncoder = passwordEncoder;
        this.jwt = jwt;
        this.csrfTokenService = csrfTokenService;
        this.customUserDetailsService = customUserDetailsService;
    }

    public void setAuthCookies(HttpServletResponse httpResponse, String token) {
        String sessionId = UUID.randomUUID().toString();
        String csrfToken = csrfTokenService.generateAndStoreToken(sessionId);

        ResponseCookie jwtCookie = ResponseCookie.from("jwt", token)
                .httpOnly(true)
                .secure(true)
                .sameSite("None")
                .path("/")
                .maxAge(Duration.ofDays(1))
                .build();

        ResponseCookie csrfCookie = ResponseCookie.from("csrf-token", csrfToken)
                .httpOnly(false)
                .secure(true)
                .sameSite("None")
                .path("/")
                .maxAge(Duration.ofDays(1))
                .build();

        ResponseCookie sessionCookie = ResponseCookie.from("sessionId", sessionId)
                .httpOnly(false)
                .secure(true)
                .sameSite("None")
                .path("/")
                .maxAge(Duration.ofDays(1))
                .build();

        httpResponse.addHeader("Set-Cookie", jwtCookie.toString());
        httpResponse.addHeader("Set-Cookie", csrfCookie.toString());
        httpResponse.addHeader("Set-Cookie", sessionCookie.toString());
    }

    @Transactional
    public String register(FinalRegistrationRequest request) {
        //decrypt the request

        if (adminRepository.existsByEmail(request.getEmail())) {
            throw new DuplicateEmailException("Admin with this email already exists");
        }

        if (organizationRepository.existsByOrganizationName(request.getOrganizationName())) {
            throw new DuplicateOrganizationException("Organization with this name already exists");
        }

        Admin admin = Admin.builder()
                .name(request.getAdminFullName())
                .email(request.getEmail())
                .phone(request.getPhoneNumber())
                .nic(request.getNationalId())
                .password(passwordEncoder.encode(request.getPassword()))
                .build();

        adminRepository.save(admin);

        Organization org = Organization.builder()
                .organizationName(request.getOrganizationName())
                .status(Status.INACTIVE)
                .admin(admin)
                .build();

        organizationRepository.save(org);

        Map<String, Object> claims = new HashMap<>();
        claims.put("adminId", admin.getId());
        claims.put("organizationId", org.getId());
        claims.put("isPackageActive", false);

        return jwt.generateToken(claims, admin);
    }

    public LogInfoResponse login(LoginRequest loginRequest)  {

        // get the UserDetails object
        UserDetails userDetails = customUserDetailsService.loadUserByUsername(loginRequest.getEmail());
        if (userDetails == null) {
            throw new InvalidEmailException("Admin not found with email: " + loginRequest.getEmail());
        }
        // check if the user is an instance of Admin or SuperAdmin
        if ((userDetails instanceof Admin)) {

            logger.info("Identified user as Admin: " + loginRequest.getEmail());
            Admin admin = (Admin) userDetails;
            if (!passwordEncoder.matches(loginRequest.getPassword(), admin.getPassword())) {
                throw new InvalidPasswordException("Invalid password");
            }

            Organization org = admin.getOrganization();
            if (org == null) {
                throw new RuntimeException("Admin does not belong to any organization");
            }

            SecurityContextHolder.getContext().setAuthentication(
                    UsernamePasswordAuthenticationToken.authenticated(admin, null, admin.getAuthorities())
            );

            AdminDTO adminDTO = new AdminDTO(
                    admin.getId(),
                    admin.getName(),
                    admin.getEmail(),
                    admin.getPhone(),
                    admin.isEmailVerified()
            );

            String subscriptionPlan = org.getSubscriptionPlan() != null ? org.getSubscriptionPlan().getName() : "No Plan";


            OrganizationDTO orgDTO = new OrganizationDTO(
                    org.getId(),
                    org.getOrganizationName(),
                    subscriptionPlan,
                    org.getCreatedAt(),
                    org.getNextRenewalDate(),
                    org.getSubdomain(),
                    org.getPortalUrl(),
                    org.getMaxMemberCount(),
                    org.getCurrentMemberCount(),
                    org.getStatus()
            );
            logger.info("created OrganizationDTO");

            Map<String, Object> claims = new HashMap<>();
            claims.put("adminEmail", admin.getEmail());
            claims.put("organizationId", org.getId());
            claims.put("isPackageActive", org.getStatus() == Status.ACTIVE);

            return new LogInfoResponse(adminDTO, orgDTO, jwt.generateToken(claims, admin));

        }

        if((userDetails instanceof SuperAdmin)) {
            logger.info("Identified user as SuperAdmin: " + loginRequest.getEmail());
            SuperAdmin superAdmin = (SuperAdmin) userDetails;
            if (!passwordEncoder.matches(loginRequest.getPassword(), superAdmin.getPassword())) {
                logger.info("invalid password for SuperAdmin: " + loginRequest.getEmail());
                throw new InvalidPasswordException("Invalid password");
            }

            SecurityContextHolder.getContext().setAuthentication(
                    UsernamePasswordAuthenticationToken.authenticated(superAdmin, null, superAdmin.getAuthorities())
            );

            SuperAdminDTO superAdminDTO = new SuperAdminDTO(
                    superAdmin.getId(),
                    superAdmin.getName(),
                    superAdmin.getEmail()
            );

            Map<String, Object> claims = new HashMap<>();
            claims.put("adminEmail", superAdmin.getEmail());
            logger.info("sending claims: " + claims);
            return new LogInfoResponse(superAdminDTO, null, jwt.generateToken(claims, superAdmin));

        }

        else{throw new InvalidEmailException("Admin or SuperAdmin not found with email: " + loginRequest.getEmail());}




    }

    public GlobalAuthResponse verifyAdminCredentials(@Valid LoginRequest loginRequest) {
        // Check if the admin exists
        Admin admin = (Admin) adminRepository.findByEmail(loginRequest.getEmail())
                .orElseThrow(() -> new InvalidEmailException("Admin not found with email: " + loginRequest.getEmail()));
        // Verify the password
        if (!passwordEncoder.matches(loginRequest.getPassword(), admin.getPassword())) {
            throw new InvalidPasswordException("Invalid password for admin: " + loginRequest.getEmail());
        }
        logger.info("Admin credentials verified for: " + loginRequest.getEmail());
        // Check if the admin belongs to an organization
        Organization org = admin.getOrganization();
        if (org == null) {
            throw new RuntimeException("Admin does not belong to any organization");
        }
        // Create DTOs for admin and organization
        AdminGlobalDTO adminDTO = new AdminGlobalDTO(
                admin.getName(),
                admin.getEmail(),
                admin.getPassword(),
                admin.getPhone()
        );
        OrganizationGlobalDTO orgDTO = new OrganizationGlobalDTO(
                org.getId(),
                org.getOrganizationName(),
                org.getMaxMemberCount(),
                org.getCurrentMemberCount(),
                org.getStatus(),
                org.isMembershipFree(),
                org.isDeleted()
        );
        logger.info("created organization DTO for: " + org.getOrganizationName());
        logger.info("created admin DTO for: " + admin.getName());
        // Create the response object
        // Return the response
        return new GlobalAuthResponse(true, adminDTO, orgDTO);

    }
}
