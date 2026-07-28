package com.hyperlofy.backend.ai.platform.repository;

import com.hyperlofy.backend.ai.platform.entity.AiInferenceLog;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.UUID;

@Repository
public interface AiInferenceLogRepository extends JpaRepository<AiInferenceLog, UUID> {
    List<AiInferenceLog> findByModelNameOrderByCreatedAtDesc(String modelName);
}
