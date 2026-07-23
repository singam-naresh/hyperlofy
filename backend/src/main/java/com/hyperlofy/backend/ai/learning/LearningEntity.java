package com.hyperlofy.backend.ai.learning;

import com.hyperlofy.backend.common.entity.BaseEntity;
import com.hyperlofy.backend.user.entity.User;
import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.FetchType;
import jakarta.persistence.JoinColumn;
import jakarta.persistence.ManyToOne;
import jakarta.persistence.Table;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

import java.time.OffsetDateTime;
import java.util.UUID;

@Entity
@Table(name = "learning_events")
@Getter
@Setter
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class LearningEntity extends BaseEntity {

    @Column(name = "learning_id", nullable = false, unique = true)
    private UUID learningId;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "customer_id", nullable = false)
    private User customer;

    @Column(name = "conversation_id")
    private UUID conversationId;

    @Column(name = "order_id")
    private UUID orderId;

    @Column(name = "merchant_id")
    private UUID merchantId;

    @Column(name = "recommendation_id")
    private UUID recommendationId;

    @Column(name = "learning_type", nullable = false, length = 60)
    private LearningType learningType;

    @Column(name = "score", nullable = false)
    private double score;

    @Column(name = "confidence", nullable = false)
    private double confidence;

    @Column(name = "recency", nullable = false)
    private double recency;

    @Column(name = "frequency", nullable = false)
    private double frequency;

    @Column(name = "details", columnDefinition = "TEXT")
    private String details;

    @Column(name = "event_at", nullable = false)
    private OffsetDateTime eventAt;
}
