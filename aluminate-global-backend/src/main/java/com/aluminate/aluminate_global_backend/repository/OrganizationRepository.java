package com.aluminate.aluminate_global_backend.repository;

import com.aluminate.aluminate_global_backend.model.Organization;
import jakarta.validation.constraints.NotBlank;
import org.springframework.data.jpa.repository.JpaRepository;

public interface OrganizationRepository extends JpaRepository<Organization, Long> {

    boolean existsByOrganizationName(@NotBlank(message = "cannot be blank") String organizationName);
}
