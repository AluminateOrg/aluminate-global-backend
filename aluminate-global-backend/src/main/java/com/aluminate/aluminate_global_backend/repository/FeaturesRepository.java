package com.aluminate.aluminate_global_backend.repository;

//import com.aluminate.aluminate_global_backend.model.Features;
import com.aluminate.aluminate_global_backend.model.PlanFeature;
import org.springframework.data.jpa.repository.JpaRepository;

public interface FeaturesRepository extends JpaRepository<PlanFeature, Long> {
}
