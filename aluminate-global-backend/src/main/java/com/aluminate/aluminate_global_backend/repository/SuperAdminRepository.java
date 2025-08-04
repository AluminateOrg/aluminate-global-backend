package com.aluminate.aluminate_global_backend.repository;

import com.aluminate.aluminate_global_backend.model.SuperAdmin;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.Optional;

public interface SuperAdminRepository extends JpaRepository<SuperAdmin, Long> {
    boolean existsSuperAdminByEmail(String email);

    Optional<SuperAdmin> findSuperAdminByEmail(String email);

    boolean existsByEmail(String email);

    SuperAdmin findByEmail(String email);
}
