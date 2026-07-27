package com.hyperlofy.backend.ai.genai.repository;

import com.hyperlofy.backend.ai.genai.entity.KnowledgeDocumentChunk;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.UUID;

@Repository
public interface KnowledgeDocumentChunkRepository extends JpaRepository<KnowledgeDocumentChunk, UUID> {
    List<KnowledgeDocumentChunk> findByDocumentIdOrderByChunkIndexAsc(UUID documentId);
}
