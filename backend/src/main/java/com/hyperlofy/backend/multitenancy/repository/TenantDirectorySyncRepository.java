package com.hyperlofy.backend.multitenancy.repository;

import com.hyperlofy.backend.multitenancy.entity.TenantDirectorySync;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.UUID;

@Repository
public interface TenantDirectorySyncRepository extends JpaRepository<TenantDirectorySync, UUID> {
    List<TenantDirectorySync> findByTenantId(UUID tenantId);
}
