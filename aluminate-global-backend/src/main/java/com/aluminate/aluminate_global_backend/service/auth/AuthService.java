package com.aluminate.aluminate_global_backend.service.auth;

import com.aluminate.aluminate_global_backend.config.util.Jwt;
import com.aluminate.aluminate_global_backend.dto.registration.RegistrationRequest;
import com.aluminate.aluminate_global_backend.model.Admin;
import com.aluminate.aluminate_global_backend.model.Organization;
import com.aluminate.aluminate_global_backend.model.Status;
import com.aluminate.aluminate_global_backend.repository.AdminRepository;
import com.aluminate.aluminate_global_backend.repository.OrganizationRepository;
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
            throw new RuntimeException("Admin with this email already exists");
        }
        if (organizationRepository.existsByOrganizationName(request.getOrganizationName())) {
            throw new RuntimeException("Organization with this name already exists");
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

}
