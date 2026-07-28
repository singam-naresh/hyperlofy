package com.hyperlofy.backend.multitenancy.repository;

import com.hyperlofy.backend.multitenancy.entity.TenantLicenseAllocation;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.UUID;

@Repository
public interface TenantLicenseAllocationRepository extends JpaRepository<TenantLicenseAllocation, UUID> {
    List<TenantLicenseAllocation> findByTenantId(UUID tenantId);
}
