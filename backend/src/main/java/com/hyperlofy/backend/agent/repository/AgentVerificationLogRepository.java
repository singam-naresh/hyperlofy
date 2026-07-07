package com.hyperlofy.backend.agent.repository;

import com.hyperlofy.backend.agent.entity.AgentVerificationLog;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.UUID;

@Repository
public interface AgentVerificationLogRepository extends JpaRepository<AgentVerificationLog, UUID> {
    List<AgentVerificationLog> findByAgentId(UUID agentId);
}
