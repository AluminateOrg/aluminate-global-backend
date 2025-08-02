package com.aluminate.aluminate_global_backend.config;

import com.aluminate.aluminate_global_backend.config.util.Jwt;
import com.aluminate.aluminate_global_backend.model.Admin;
import com.aluminate.aluminate_global_backend.model.Organization;
import com.aluminate.aluminate_global_backend.repository.AdminRepository;
import com.aluminate.aluminate_global_backend.repository.OrganizationRepository;
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

    @Value("${api.prefix}")
    private String apiPrefix;

    public JwtAuthenticationFilter(Jwt jwtUtil,
                                   AdminRepository adminRepository,
                                   OrganizationRepository organizationRepository,
                                   CsrfTokenService csrfTokenService) {
        this.jwtUtil = jwtUtil;
        this.adminRepository = adminRepository;
        this.organizationRepository = organizationRepository;
        this.csrfTokenService = csrfTokenService;
    }

    private static final Logger logger = LoggerFactory.getLogger(JwtAuthenticationFilter.class);


    @Override
    protected void doFilterInternal(@NonNull HttpServletRequest request,
                                    @NonNull HttpServletResponse response,
                                    @NonNull FilterChain filterChain)
            throws ServletException, IOException {

        String path = request.getRequestURI();
        logger.info("JwtAuthenticationFilter called for path: " + path);


        if (!path.startsWith(apiPrefix + "/admin/")) {
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

        // JWT validation remains as before
        String jwt = extractJwtFromCookie(request);

        if (jwt == null) {
            logger.error("No JWT found in request");
            unauthorized(response, "Missing JWT token");
            return;
        }

        try {
            Claims claims = jwtUtil.extractAllClaims(jwt);

            Long adminId = claims.get("adminId", Long.class);
            Long orgId = claims.get("organizationId", Long.class);
            Boolean isPackageActive = claims.get("isPackageActive", Boolean.class);

            if (adminId == null || orgId == null) {
                unauthorized(response, "Invalid token claims");
                logger.error("Invalid token claims");
                return;
            }

            Optional<Admin> adminOpt = adminRepository.findById(adminId);
            Optional<Organization> orgOpt = organizationRepository.findById(orgId);

            if (adminOpt.isEmpty() || orgOpt.isEmpty()) {
                unauthorized(response, "Invalid admin or organization");
                logger.error("Invalid admin or organization");
                return;
            }

            Admin admin = adminOpt.get();
            Organization org = orgOpt.get();




            boolean requireActivePackage = requiresActivePackage(request.getRequestURI());

            if (requireActivePackage && (isPackageActive == null || !isPackageActive)) {
                forbidden(response, "Package inactive, access denied");
                logger.error("Package inactive, access denied");
                return;
            }

            try{
                UsernamePasswordAuthenticationToken auth =
                        new UsernamePasswordAuthenticationToken(admin, null, admin.getAuthorities());

                SecurityContextHolder.getContext().setAuthentication(auth);

                filterChain.doFilter(request, response);
                logger.info("Successfully passed JWT authentication for admin: " + admin.getEmail());

            }catch (Exception e){
                logger.error(e.getMessage());
                throw new RuntimeException(e.getMessage());
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
