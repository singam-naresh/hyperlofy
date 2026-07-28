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
@Table(name = "vector_embeddings")
@SQLDelete(sql = "UPDATE vector_embeddings SET deleted = true WHERE id = ?")
@SQLRestriction("deleted = false")
@Getter
@Setter
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class VectorEmbedding extends BaseEntity {

    @Column(name = "source_doc_id", nullable = false, length = 150)
    private String sourceDocId;

    @Column(name = "source_doc_type", nullable = false, length = 80)
    private String sourceDocType; // KNOWLEDGE_ARTICLE, DOCUMENT, PRODUCT, MERCHANT

    @Builder.Default
    @Column(name = "chunk_index", nullable = false)
    private Integer chunkIndex = 0;

    @Column(name = "chunk_text", nullable = false, columnDefinition = "TEXT")
    private String chunkText;

    @Column(name = "embedding_vector", nullable = false, columnDefinition = "TEXT")
    private String embeddingVector;

    @Builder.Default
    @Column(name = "embedding_model", nullable = false, length = 100)
    private String embeddingModel = "gemini-text-embedding";

    @Builder.Default
    @Column(name = "embedding_dimension", nullable = false)
    private Integer embeddingDimension = 768;

    @Builder.Default
    @Column(name = "similarity_score", precision = 8, scale = 6)
    private BigDecimal similarityScore = BigDecimal.ZERO;

    @Column(name = "tenant_id")
    private UUID tenantId;
}
