package com.hyperlofy.backend.search.repository;

import com.hyperlofy.backend.search.entity.RagChunk;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.UUID;

@Repository
public interface RagChunkRepository extends JpaRepository<RagChunk, UUID> {
    List<RagChunk> findBySourceId(String sourceId);
    List<RagChunk> findBySourceType(String sourceType);
}
