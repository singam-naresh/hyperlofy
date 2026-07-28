package com.hyperlofy.backend.engagement.entity;

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
@Table(name = "product_recommendations")
@SQLDelete(sql = "UPDATE product_recommendations SET deleted = true WHERE id = ?")
@SQLRestriction("deleted = false")
@Getter
@Setter
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class ProductRecommendation extends BaseEntity {

    @Column(name = "recommendation_code", nullable = false, unique = true, length = 100)
    private String recommendationCode;

    @Column(name = "customer_id", nullable = false)
    private UUID customerId;

    @Column(name = "product_id", nullable = false)
    private UUID productId;

    @Builder.Default
    @Column(name = "recommendation_type", nullable = false, length = 80)
    private String recommendationType = "COLLABORATIVE_FILTERING"; // COLLABORATIVE_FILTERING, SIMILAR_PURCHASE, RECENTLY_VIEWED, FREQUENTLY_BOUGHT_TOGETHER

    @Builder.Default
    @Column(name = "similarity_score", nullable = false, precision = 5, scale = 4)
    private BigDecimal similarityScore = new BigDecimal("0.9500");

    @Builder.Default
    @Column(name = "ai_model_version", nullable = false, length = 100)
    private String aiModelVersion = "gemini-recommendation-v3";

    @Builder.Default
    @Column(name = "status", nullable = false, length = 30)
    private String status = "PENDING"; // PENDING, VIEWED, ACCEPTED, REJECTED

    @Column(name = "tenant_id")
    private UUID tenantId;
}
