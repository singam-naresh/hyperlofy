package com.hyperlofy.backend.agent.repository;

import com.hyperlofy.backend.agent.entity.AgentPayoutProfile;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.Optional;
import java.util.UUID;

@Repository
public interface AgentPayoutProfileRepository extends JpaRepository<AgentPayoutProfile, UUID> {
    Optional<AgentPayoutProfile> findByAgentId(UUID agentId);
}
