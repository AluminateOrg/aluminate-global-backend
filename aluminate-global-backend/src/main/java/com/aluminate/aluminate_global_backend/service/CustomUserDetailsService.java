package com.aluminate.aluminate_global_backend.service;

import com.aluminate.aluminate_global_backend.repository.AdminRepository;
import com.aluminate.aluminate_global_backend.repository.SuperAdminRepository;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.core.userdetails.UserDetailsService;
import org.springframework.security.core.userdetails.UsernameNotFoundException;
import org.springframework.stereotype.Service;

@Service
public class CustomUserDetailsService implements UserDetailsService {
    private final AdminRepository adminRepo;
    private final SuperAdminRepository superAdminRepo;

    public CustomUserDetailsService(AdminRepository adminRepo, SuperAdminRepository superAdminRepo) {
        this.adminRepo = adminRepo;
        this.superAdminRepo = superAdminRepo;
    }

    @Override
    public UserDetails loadUserByUsername(String email) throws UsernameNotFoundException {
        return adminRepo.findAdminByEmail(email)
                .<UserDetails>map(admin -> admin)
                .or(() -> superAdminRepo.findSuperAdminByEmail(email).map(superAdmin -> superAdmin))
                .orElseThrow(() -> new UsernameNotFoundException("User not found"));
    }
}
