package com.hyperlofy.backend.ai.recommendation;

import com.hyperlofy.backend.common.entity.BaseEntity;
import jakarta.persistence.*;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

import java.time.OffsetDateTime;
import java.util.UUID;

@Entity
@Table(name = "recommendations", indexes = {
        @Index(name = "idx_recommendations_customer", columnList = "customer_id"),
        @Index(name = "idx_recommendations_conversation", columnList = "conversation_id"),
        @Index(name = "idx_recommendations_order_draft", columnList = "order_draft_id"),
        @Index(name = "idx_recommendations_accepted", columnList = "accepted"),
        @Index(name = "idx_recommendations_dismissed", columnList = "dismissed")
})
@Getter
@Setter
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class RecommendationEntity extends BaseEntity {

    @Column(name = "recommendation_id", nullable = false, unique = true, updatable = false)
    private UUID recommendationId;

    @Column(name = "customer_id", nullable = false)
    private UUID customerId;

    @Column(name = "conversation_id")
    private UUID conversationId;

    @Column(name = "order_draft_id")
    private UUID orderDraftId;

    @Column(name = "recommended_item", nullable = false, length = 300)
    private String recommendedItem;

    @Column(name = "reason", nullable = false, length = 80)
    @Enumerated(EnumType.STRING)
    private RecommendationReason reason;

    @Column(name = "recommendation_type", nullable = false, length = 80)
    @Enumerated(EnumType.STRING)
    private RecommendationType recommendationType;

    @Column(name = "score", nullable = false)
    private double score;

    @Column(name = "accepted", nullable = false)
    private boolean accepted;

    @Column(name = "dismissed", nullable = false)
    private boolean dismissed;
}
