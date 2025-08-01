package com.aluminate.aluminate_global_backend.service.auth;

import com.aluminate.aluminate_global_backend.config.exception.DuplicateEmailException;
import com.aluminate.aluminate_global_backend.config.exception.DuplicateOrganizationException;
import com.aluminate.aluminate_global_backend.config.util.Jwt;
import com.aluminate.aluminate_global_backend.dto.getInfo.AdminDTO;
import com.aluminate.aluminate_global_backend.dto.getInfo.InfoResponse;
import com.aluminate.aluminate_global_backend.dto.getInfo.LogInfoResponse;
import com.aluminate.aluminate_global_backend.dto.getInfo.OrganizationDTO;
import com.aluminate.aluminate_global_backend.dto.login.LoginRequest;
import com.aluminate.aluminate_global_backend.dto.registration.RegistrationRequest;
import com.aluminate.aluminate_global_backend.model.Admin;
import com.aluminate.aluminate_global_backend.model.Organization;
import com.aluminate.aluminate_global_backend.model.Status;
import com.aluminate.aluminate_global_backend.repository.AdminRepository;
import com.aluminate.aluminate_global_backend.repository.OrganizationRepository;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;

import java.util.HashMap;
import java.util.Map;

@Service
public class AuthService {

    private final OrganizationRepository organizationRepository;
    private final AdminRepository adminRepository;
    private final PasswordEncoder passwordEncoder;
    private final Jwt jwt;
    private final Logger logger =  LoggerFactory.getLogger(AuthService.class);

    public AuthService(
            OrganizationRepository organizationRepository,
            AdminRepository adminRepository,
            PasswordEncoder passwordEncoder,
            Jwt jwt
    ) {
        this.organizationRepository = organizationRepository;
        this.adminRepository = adminRepository;
        this.passwordEncoder = passwordEncoder;
        this.jwt = jwt;
    }

    public String register(RegistrationRequest request) {
        //check if admin or organization already exists
        if (adminRepository.existsByEmail(request.getEmail())) {
            throw new DuplicateEmailException("Admin with this email already exists");
        }
        if (organizationRepository.existsByOrganizationName(request.getOrganizationName())) {
            throw new DuplicateOrganizationException("Organization with this name already exists");
        }
        // Create Admin and Organization entities
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
                .status(Status.ACTIVE)
                .admin(admin)
                .build();

        organizationRepository.save(org);

        // Create JWT claims
        Map<String, Object> claims = new HashMap<>();
        claims.put("adminId", admin.getId());
        claims.put("organizationId", org.getId());
        claims.put("isPackageActive", org.getStatus() == Status.ACTIVE);

        return jwt.generateToken(claims, admin);
    }

    public LogInfoResponse login(LoginRequest loginRequest) {
        try{
            Admin admin = (Admin) adminRepository.findByEmail(loginRequest.getEmail())
                    .orElseThrow(() -> new RuntimeException("Admin not found with email: " + loginRequest.getEmail()));

            // Check if the password matches
            if (!passwordEncoder.matches(loginRequest.getPassword(), admin.getPassword())) {
                throw new RuntimeException("Invalid password");
            }
            // Find the organization associated with the admin
            Organization org = admin.getOrganization();
            if (org == null) {
                throw new RuntimeException("Admin does not belong to any organization");

            }

            // Set the admin in the security context
            SecurityContextHolder.getContext().setAuthentication(UsernamePasswordAuthenticationToken.authenticated(
                    admin, null, admin.getAuthorities()));

            //set AdminDTO and OrganizationDTO
            AdminDTO adminDTO = new AdminDTO(
                    admin.getId(),
                    admin.getName(),
                    admin.getEmail(),
                    admin.getPhone(),
                    admin.isEmailVerified()
            );
            logger.info("created AdminDTO");

            OrganizationDTO orgDTO = new OrganizationDTO(
                    org.getId(),
                    org.getOrganizationName(),
                    org.getSubscriptionPlan(),
                    org.getCreatedAt(),
                    org.getNextRenewalDate(),
                    org.getSubdomain(),
                    org.getPortalUrl(),
                    org.getMaxMemberCount(),
                    org.getCurrentMemberCount(),
                    org.getStatus()
            );



            // Create JWT claims
            Map<String, Object> claims = new HashMap<>();
            claims.put("adminId", admin.getId());
            claims.put("organizationId", admin.getOrganization().getId());
            claims.put("isPackageActive", admin.getOrganization().getStatus() == Status.ACTIVE);

            return new LogInfoResponse(adminDTO,orgDTO, jwt.generateToken(claims, admin));
        }catch (Exception e){
            throw new RuntimeException(e.getMessage());
        }
        // Find the admin by email

    }

}
