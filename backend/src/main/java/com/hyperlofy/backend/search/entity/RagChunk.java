package com.hyperlofy.backend.search.entity;

import com.hyperlofy.backend.common.entity.BaseEntity;
import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.Table;
import lombok.*;
import org.hibernate.annotations.SQLDelete;
import org.hibernate.annotations.SQLRestriction;

import java.math.BigDecimal;
import java.util.UUID;

@Entity
@Table(name = "rag_chunks")
@SQLDelete(sql = "UPDATE rag_chunks SET deleted = true WHERE id = ?")
@SQLRestriction("deleted = false")
@Getter
@Setter
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class RagChunk extends BaseEntity {

    @Column(name = "source_id", nullable = false, length = 150)
    private String sourceId;

    @Column(name = "source_type", nullable = false, length = 80)
    private String sourceType; // DOCUMENT, KNOWLEDGE_ARTICLE, WORKFLOW, CASE

    @Builder.Default
    @Column(name = "chunk_index", nullable = false)
    private Integer chunkIndex = 0;

    @Column(name = "chunk_text", nullable = false, columnDefinition = "TEXT")
    private String chunkText;

    @Column(name = "vector_embedding", columnDefinition = "TEXT")
    private String vectorEmbedding;

    @Builder.Default
    @Column(name = "relevance_score", nullable = false, precision = 5, scale = 4)
    private BigDecimal relevanceScore = new BigDecimal("0.9000");

    @Column(name = "tenant_id")
    private UUID tenantId;
}
