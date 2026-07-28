package com.hyperlofy.backend.multitenancy.repository;

import com.hyperlofy.backend.multitenancy.entity.TenantDataResidency;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.Optional;
import java.util.UUID;

@Repository
public interface TenantDataResidencyRepository extends JpaRepository<TenantDataResidency, UUID> {
    Optional<TenantDataResidency> findByTenantId(UUID tenantId);
}
