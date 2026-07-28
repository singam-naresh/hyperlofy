package com.hyperlofy.backend.ai.platform.repository;

import com.hyperlofy.backend.ai.platform.entity.AiModelRegistry;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.Optional;
import java.util.UUID;

@Repository
public interface AiModelRegistryRepository extends JpaRepository<AiModelRegistry, UUID> {
    Optional<AiModelRegistry> findByModelName(String modelName);
}
