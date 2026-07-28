package com.hyperlofy.backend.admin.repository;

import com.hyperlofy.backend.admin.entity.AdminAgentWorkload;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.Optional;
import java.util.UUID;

@Repository
public interface AdminAgentWorkloadRepository extends JpaRepository<AdminAgentWorkload, UUID> {
    Optional<AdminAgentWorkload> findByAgentUser(String agentUser);
}
