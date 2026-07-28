package com.hyperlofy.backend.ai.platform.repository;

import com.hyperlofy.backend.ai.platform.entity.AiPrompt;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.Optional;
import java.util.UUID;

@Repository
public interface AiPromptRepository extends JpaRepository<AiPrompt, UUID> {
    Optional<AiPrompt> findByPromptKey(String promptKey);
}
