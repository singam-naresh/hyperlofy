package com.hyperlofy.backend.multitenancy.repository;

import com.hyperlofy.backend.multitenancy.entity.TenantIdentityProvider;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.UUID;

@Repository
public interface TenantIdentityProviderRepository extends JpaRepository<TenantIdentityProvider, UUID> {
    List<TenantIdentityProvider> findByTenantId(UUID tenantId);
}
