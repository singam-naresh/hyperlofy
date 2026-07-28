package com.hyperlofy.backend.search.entity;

import com.hyperlofy.backend.common.entity.BaseEntity;
import jakarta.persistence.*;
import lombok.*;
import org.hibernate.annotations.SQLDelete;
import org.hibernate.annotations.SQLRestriction;

import java.math.BigDecimal;
import java.util.UUID;

@Entity
@Table(name = "search_documents")
@SQLDelete(sql = "UPDATE search_documents SET deleted = true WHERE id = ?")
@SQLRestriction("deleted = false")
@Getter
@Setter
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class SearchDocument extends BaseEntity {

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "index_id", nullable = false)
    private SearchIndex index;

    @Column(name = "doc_external_id", nullable = false, length = 150)
    private String docExternalId;

    @Column(name = "doc_type", nullable = false, length = 80)
    private String docType;

    @Column(name = "title", nullable = false, length = 500)
    private String title;

    @Column(name = "body_excerpt", columnDefinition = "TEXT")
    private String bodyExcerpt;

    @Column(name = "embedding_vector", columnDefinition = "TEXT")
    private String embeddingVector;

    @Builder.Default
    @Column(name = "embedding_model", length = 100)
    private String embeddingModel = "gemini-text-embedding";

    @Column(name = "tags", length = 500)
    private String tags;

    @Column(name = "category", length = 100)
    private String category;

    @Column(name = "tenant_id")
    private UUID tenantId;

    @Builder.Default
    @Column(name = "relevance_score", precision = 8, scale = 4)
    private BigDecimal relevanceScore = new BigDecimal("1.0000");

    @Builder.Default
    @Column(name = "is_published", nullable = false)
    private Boolean isPublished = true;

    @Column(name = "source_url", length = 500)
    private String sourceUrl;
}
