package com.hyperlofy.backend.ai.platform.entity;

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
@Table(name = "ai_recommendations")
@SQLDelete(sql = "UPDATE ai_recommendations SET deleted = true WHERE id = ?")
@SQLRestriction("deleted = false")
@Getter
@Setter
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class AiRecommendation extends BaseEntity {

    @Column(name = "user_id", nullable = false)
    private UUID userId;

    @Column(name = "recommendation_type", nullable = false, length = 50)
    private String recommendationType; // PRODUCT, MERCHANT, UPSELL, CROSS_SELL

    @Column(name = "recommended_entity_id", nullable = false)
    private UUID recommendedEntityId;

    @Builder.Default
    @Column(name = "confidence_score", nullable = false, precision = 5, scale = 4)
    private BigDecimal confidenceScore = new BigDecimal("0.9200");
}
