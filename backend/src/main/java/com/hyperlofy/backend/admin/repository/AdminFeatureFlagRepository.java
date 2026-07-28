package com.hyperlofy.backend.admin.repository;

import com.hyperlofy.backend.admin.entity.AdminFeatureFlag;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.Optional;
import java.util.UUID;

@Repository
public interface AdminFeatureFlagRepository extends JpaRepository<AdminFeatureFlag, UUID> {
    Optional<AdminFeatureFlag> findByFlagKey(String flagKey);
}
