package com.hyperlofy.backend.search.entity;

import com.hyperlofy.backend.common.entity.BaseEntity;
import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.Table;
import lombok.*;
import org.hibernate.annotations.SQLDelete;
import org.hibernate.annotations.SQLRestriction;

import java.util.UUID;

@Entity
@Table(name = "search_analytics")
@SQLDelete(sql = "UPDATE search_analytics SET deleted = true WHERE id = ?")
@SQLRestriction("deleted = false")
@Getter
@Setter
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class SearchAnalytics extends BaseEntity {

    @Column(name = "query_text", nullable = false, length = 500)
    private String queryText;

    @Column(name = "domain", length = 80)
    private String domain;

    @Builder.Default
    @Column(name = "result_count", nullable = false)
    private Integer resultCount = 0;

    @Builder.Default
    @Column(name = "is_zero_result", nullable = false)
    private Boolean isZeroResult = false;

    @Column(name = "selected_doc_id", length = 150)
    private String selectedDocId;

    @Builder.Default
    @Column(name = "response_time_ms", nullable = false)
    private Integer responseTimeMs = 0;

    @Builder.Default
    @Column(name = "search_type", nullable = false, length = 30)
    private String searchType = "FULL_TEXT"; // FULL_TEXT, SEMANTIC, HYBRID, FACETED

    @Column(name = "user_id")
    private UUID userId;

    @Column(name = "tenant_id")
    private UUID tenantId;

    @Column(name = "session_id", length = 100)
    private String sessionId;
}
