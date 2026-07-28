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
@Table(name = "search_conversations")
@SQLDelete(sql = "UPDATE search_conversations SET deleted = true WHERE id = ?")
@SQLRestriction("deleted = false")
@Getter
@Setter
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class SearchConversation extends BaseEntity {

    @Column(name = "conversation_code", nullable = false, unique = true, length = 100)
    private String conversationCode;

    @Column(name = "user_id", nullable = false)
    private UUID userId;

    @Column(name = "user_query", nullable = false, columnDefinition = "TEXT")
    private String userQuery;

    @Column(name = "ai_response", nullable = false, columnDefinition = "TEXT")
    private String aiResponse;

    @Builder.Default
    @Column(name = "intent_type", nullable = false, length = 80)
    private String intentType = "SEARCH"; // SEARCH, QA, RETRIEVAL, SUMMARY

    @Builder.Default
    @Column(name = "confidence_score", nullable = false, precision = 5, scale = 2)
    private BigDecimal confidenceScore = new BigDecimal("95.00");

    @Column(name = "citation_sources", columnDefinition = "TEXT")
    private String citationSources;

    @Column(name = "tenant_id")
    private UUID tenantId;
}
