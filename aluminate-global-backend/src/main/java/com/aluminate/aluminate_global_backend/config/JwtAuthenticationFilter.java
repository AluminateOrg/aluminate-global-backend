package com.aluminate.aluminate_global_backend.config;

import com.aluminate.aluminate_global_backend.config.util.Jwt;
import com.aluminate.aluminate_global_backend.model.Admin;
import com.aluminate.aluminate_global_backend.model.Organization;
import com.aluminate.aluminate_global_backend.model.SuperAdmin;
import com.aluminate.aluminate_global_backend.repository.AdminRepository;
import com.aluminate.aluminate_global_backend.repository.OrganizationRepository;
import com.aluminate.aluminate_global_backend.service.CustomUserDetailsService;
import com.aluminate.aluminate_global_backend.service.csrf.CsrfTokenService;
import io.jsonwebtoken.Claims;
import jakarta.servlet.FilterChain;
import jakarta.servlet.ServletException;
import jakarta.servlet.http.Cookie;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.stereotype.Component;
import org.springframework.web.filter.OncePerRequestFilter;
import org.springframework.lang.NonNull;


import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.io.IOException;
import java.util.Optional;

@Component
public class JwtAuthenticationFilter extends OncePerRequestFilter {
    private final Jwt jwtUtil;
    private final AdminRepository adminRepository;
    private final OrganizationRepository organizationRepository;
    private final CsrfTokenService csrfTokenService;
    private final CustomUserDetailsService customUserDetailsService;

    @Value("${api.prefix}")
    private String apiPrefix;

    public JwtAuthenticationFilter(Jwt jwtUtil,
                                   AdminRepository adminRepository,
                                   OrganizationRepository organizationRepository,
                                   CsrfTokenService csrfTokenService,
                                   CustomUserDetailsService customUserDetailsService
    ) {
        this.jwtUtil = jwtUtil;
        this.adminRepository = adminRepository;
        this.organizationRepository = organizationRepository;
        this.csrfTokenService = csrfTokenService;
        this.customUserDetailsService = customUserDetailsService;
    }

    private static final Logger logger = LoggerFactory.getLogger(JwtAuthenticationFilter.class);


    @Override
    protected void doFilterInternal(@NonNull HttpServletRequest request,
                                    @NonNull HttpServletResponse response,
                                    @NonNull FilterChain filterChain)
            throws ServletException, IOException {

        String path = request.getRequestURI();
        logger.info("JwtAuthenticationFilter called for path: " + path);


        if (!path.startsWith(apiPrefix + "/admin/") && !path.startsWith(apiPrefix + "/superAdmin/")) {
            logger.info("Skipping JWT authentication for non-admin path: " + path);
            filterChain.doFilter(request, response);
            return;
        }

        logger.info("Checking authentication for admin secure path: " + path);

        // Validate CSRF token for state-changing methods (POST, PUT, DELETE, PATCH)
        if (requiresCsrfValidation(request)) {
            String csrfTokenFromHeader = request.getHeader("X-Csrf-Token");

            String sessionId = extractSessionIdFromRequest(request); // You decide how to get sessionId, maybe cookie or header

            if (csrfTokenFromHeader == null || sessionId == null || !csrfTokenService.validateToken(sessionId, csrfTokenFromHeader)) {
                logger.error("Invalid csrf token or session id invalid");
                forbidden(response, "Invalid or missing CSRF token");
                return;
            }
        }

        // JWT validation
        String jwt = extractJwtFromCookie(request);

        if (jwt == null) {
            logger.error("No JWT found in request");
            unauthorized(response, "Missing JWT token");
            return;
        }

        //jwt validation handling

        try {
            Claims claims = jwtUtil.extractAllClaims(jwt);

            String adminEmail = claims.get("adminEmail", String.class);
            //check if adminEmail is null
            if (adminEmail == null) {
                unauthorized(response, "Invalid token claims");
                logger.error("Invalid token claims: adminEmail is null");
                return;
            }
            //check if admin is a super admin or admin
            UserDetails userDetails = customUserDetailsService.loadUserByUsername(adminEmail);
            if (userDetails == null) {
                unauthorized(response, "Invalid admin email");
                logger.error("Invalid token claims: userDetails is null for adminEmail: " + adminEmail);
                return;
            }

            //check the instnace of Admin or SuperAdmin
            if ((userDetails instanceof Admin)) {
                Admin admin = (Admin) userDetails;
                Organization org = admin.getOrganization();
                if (org == null) {
                    unauthorized(response, "Admin does not belong to any organization");
                    logger.error("Invalid token claims: Admin does not belong to any organization for adminEmail: " + adminEmail);
                    return;
                }

                //check if organization has an active package
                boolean requireActivePackage = requiresActivePackage(request.getRequestURI());
                Boolean isPackageActive = claims.get("isPackageActive", Boolean.class);

                if (requireActivePackage && (isPackageActive == null || !isPackageActive)) {
                    forbidden(response, "Package inactive, access denied");
                    logger.error("Package inactive, access denied");
                    return;
                }

                UsernamePasswordAuthenticationToken auth =
                        new UsernamePasswordAuthenticationToken(admin, null, admin.getAuthorities());

                SecurityContextHolder.getContext().setAuthentication(auth);

                filterChain.doFilter(request, response);
                logger.info("Successfully passed JWT authentication for admin: " + admin.getEmail());

            }

            else if ((userDetails instanceof SuperAdmin)) {
                SuperAdmin superAdmin = (SuperAdmin) userDetails;

                // SuperAdmins do not require active package check
                UsernamePasswordAuthenticationToken auth =
                        new UsernamePasswordAuthenticationToken(superAdmin, null, superAdmin.getAuthorities());

                SecurityContextHolder.getContext().setAuthentication(auth);

                filterChain.doFilter(request, response);
                logger.info("Successfully passed JWT authentication for super admin: " + superAdmin.getEmail());

            } else {
                unauthorized(response, "Invalid user type");
                logger.error("Invalid user type for adminEmail: " + adminEmail);

            }

        } catch (Exception e) {
            logger.error(e.getMessage());
            unauthorized(response, "Invalid or expired JWT token");
        }
    }

    private String extractJwtFromCookie(HttpServletRequest request) {
        if (request.getCookies() == null) return null;
        for (Cookie cookie : request.getCookies()) {
            if ("jwt".equals(cookie.getName())) {
                return cookie.getValue();
            }
        }
        return null;
    }

    private boolean requiresActivePackage(String uri) {
        // Customize which routes require active package
        // Example: only require active package on sensitive routes
        return uri.startsWith(apiPrefix + "/admin/secure");
    }

    private void unauthorized(HttpServletResponse response, String message) throws IOException {
        response.setStatus(HttpServletResponse.SC_UNAUTHORIZED);
        response.setContentType("application/json");
        response.getWriter().write("{\"error\": \"" + message + "\"}");
    }

    private void forbidden(HttpServletResponse response, String message) throws IOException {
        response.setStatus(HttpServletResponse.SC_FORBIDDEN);
        response.setContentType("application/json");
        response.getWriter().write("{\"error\": \"" + message + "\"}");
    }
    private boolean requiresCsrfValidation(HttpServletRequest request) {
        // Usually only for state-changing HTTP methods
        String method = request.getMethod();
        return method.equals("POST") || method.equals("PUT") || method.equals("PATCH") || method.equals("DELETE");
    }

    private String extractSessionIdFromRequest(HttpServletRequest request) {

        //  from header:
         return request.getHeader("X-Session-Id");

    }
}
