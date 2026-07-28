package com.hyperlofy.backend.platform.repository;

import com.hyperlofy.backend.platform.entity.RetentionPolicy;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.Optional;
import java.util.UUID;

@Repository
public interface RetentionPolicyRepository extends JpaRepository<RetentionPolicy, UUID> {
    Optional<RetentionPolicy> findByPolicyName(String policyName);
}
