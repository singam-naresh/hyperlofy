package com.hyperlofy.backend.search.repository;

import com.hyperlofy.backend.search.entity.VectorEmbedding;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.UUID;

@Repository
public interface VectorEmbeddingRepository extends JpaRepository<VectorEmbedding, UUID> {
    List<VectorEmbedding> findBySourceDocId(String sourceDocId);
    List<VectorEmbedding> findBySourceDocType(String sourceDocType);
}
