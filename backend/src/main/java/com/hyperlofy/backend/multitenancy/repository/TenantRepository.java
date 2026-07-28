package com.hyperlofy.backend.multitenancy.repository;

import com.hyperlofy.backend.multitenancy.entity.Tenant;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.Optional;
import java.util.UUID;

@Repository
public interface TenantRepository extends JpaRepository<Tenant, UUID> {
    Optional<Tenant> findByTenantCode(String tenantCode);
    Optional<Tenant> findByDomainName(String domainName);
}
