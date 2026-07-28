package com.hyperlofy.backend.governance.repository;

import com.hyperlofy.backend.governance.entity.ProductionCertification;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.Optional;
import java.util.UUID;

@Repository
public interface ProductionCertificationRepository extends JpaRepository<ProductionCertification, UUID> {
    Optional<ProductionCertification> findByCertificationCode(String certificationCode);
}
