package com.hyperlofy.backend.ai.platform.repository;

import com.hyperlofy.backend.ai.platform.entity.AiGovernance;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.Optional;
import java.util.UUID;

@Repository
public interface AiGovernanceRepository extends JpaRepository<AiGovernance, UUID> {
    Optional<AiGovernance> findByModelName(String modelName);
}
