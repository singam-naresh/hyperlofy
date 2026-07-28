package com.hyperlofy.backend.platform.repository;

import com.hyperlofy.backend.platform.entity.ProductionCertification;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.Optional;
import java.util.UUID;

@Repository
public interface ProductionCertificationRepository extends JpaRepository<ProductionCertification, UUID> {
    Optional<ProductionCertification> findByMilestoneName(String milestoneName);
}
