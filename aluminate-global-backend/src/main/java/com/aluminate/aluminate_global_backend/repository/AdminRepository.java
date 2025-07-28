package com.aluminate.aluminate_global_backend.repository;

import com.aluminate.aluminate_global_backend.model.Admin;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.Optional;

public interface AdminRepository extends JpaRepository<Admin, Long> {
    boolean existsAdminByEmail(String email);
    Optional<Admin> findAdminByEmail(String email);
}
