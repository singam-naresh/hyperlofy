package com.hyperlofy.backend.ai.platform.repository;

import com.hyperlofy.backend.ai.platform.entity.AiSafetyEvent;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.UUID;

@Repository
public interface AiSafetyEventRepository extends JpaRepository<AiSafetyEvent, UUID> {
    List<AiSafetyEvent> findByViolationTypeOrderByCreatedAtDesc(String violationType);
}
