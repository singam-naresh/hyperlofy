package com.hyperlofy.backend.ai.platform.repository;

import com.hyperlofy.backend.ai.platform.entity.AiAgentRun;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.UUID;

@Repository
public interface AiAgentRunRepository extends JpaRepository<AiAgentRun, UUID> {
    List<AiAgentRun> findByAgentNameOrderByCreatedAtDesc(String agentName);
}
