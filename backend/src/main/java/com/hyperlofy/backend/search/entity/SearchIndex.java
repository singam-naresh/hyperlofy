package com.hyperlofy.backend.search.entity;

import com.hyperlofy.backend.common.entity.BaseEntity;
import jakarta.persistence.*;
import lombok.*;
import org.hibernate.annotations.SQLDelete;
import org.hibernate.annotations.SQLRestriction;

import java.time.OffsetDateTime;
import java.util.UUID;

@Entity
@Table(name = "search_indexes")
@SQLDelete(sql = "UPDATE search_indexes SET deleted = true WHERE id = ?")
@SQLRestriction("deleted = false")
@Getter
@Setter
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class SearchIndex extends BaseEntity {

    @Column(name = "index_name", nullable = false, unique = true, length = 150)
    private String indexName;

    @Column(name = "index_alias", length = 150)
    private String indexAlias;

    @Column(name = "domain", nullable = false, length = 80)
    private String domain; // MERCHANTS, PRODUCTS, ORDERS, CUSTOMERS, KNOWLEDGE, DOCUMENTS, WORKFLOWS, CASES, AUDIT

    @Builder.Default
    @Column(name = "status", nullable = false, length = 30)
    private String status = "ACTIVE"; // ACTIVE, REINDEXING, WARM, COLD, ARCHIVED

    @Builder.Default
    @Column(name = "document_count", nullable = false)
    private Long documentCount = 0L;

    @Builder.Default
    @Column(name = "size_bytes", nullable = false)
    private Long sizeBytes = 0L;

    @Builder.Default
    @Column(name = "version", nullable = false)
    private Integer version = 1;

    @Column(name = "tenant_id")
    private UUID tenantId;

    @Column(name = "last_reindexed_at")
    private OffsetDateTime lastReindexedAt;
}
