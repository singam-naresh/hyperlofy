package com.hyperlofy.backend.ai.platform.repository;

import com.hyperlofy.backend.ai.platform.entity.AiMemoryStore;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.UUID;

@Repository
public interface AiMemoryStoreRepository extends JpaRepository<AiMemoryStore, UUID> {
    List<AiMemoryStore> findByUserIdOrderByCreatedAtDesc(UUID userId);
}
