package com.hyperlofy.backend.security.repository;

import com.hyperlofy.backend.security.entity.SecurityPolicy;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.Optional;
import java.util.UUID;

@Repository
public interface SecurityPolicyRepository extends JpaRepository<SecurityPolicy, UUID> {
    Optional<SecurityPolicy> findByPolicyCode(String policyCode);
}
