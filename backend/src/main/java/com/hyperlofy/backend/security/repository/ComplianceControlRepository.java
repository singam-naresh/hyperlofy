package com.hyperlofy.backend.security.repository;

import com.hyperlofy.backend.security.entity.ComplianceControl;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.Optional;
import java.util.UUID;

@Repository
public interface ComplianceControlRepository extends JpaRepository<ComplianceControl, UUID> {
    Optional<ComplianceControl> findByControlCode(String controlCode);
}
