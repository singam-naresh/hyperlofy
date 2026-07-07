package com.hyperlofy.backend.agent.repository;

import com.hyperlofy.backend.agent.entity.AgentProfile;
import com.hyperlofy.backend.agent.entity.VerificationStatus;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.Optional;
import java.util.UUID;

@Repository
public interface AgentRepository extends JpaRepository<AgentProfile, UUID> {
    Optional<AgentProfile> findByUserId(UUID userId);
    List<AgentProfile> findByVerificationStatus(VerificationStatus status);
    boolean existsByPanNumber(String panNumber);
    boolean existsByAadhaarNumber(String aadhaarNumber);
}
