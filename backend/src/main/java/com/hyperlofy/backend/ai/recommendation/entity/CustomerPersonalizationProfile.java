package com.hyperlofy.backend.ai.recommendation.entity;

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
@Table(name = "customer_personalization_profiles")
@SQLDelete(sql = "UPDATE customer_personalization_profiles SET deleted = true WHERE id = ?")
@SQLRestriction("deleted = false")
@Getter
@Setter
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class CustomerPersonalizationProfile extends BaseEntity {

    @Column(name = "user_id", nullable = false, unique = true)
    private UUID userId;

    @Column(name = "preferred_category_ids", columnDefinition = "TEXT")
    private String preferredCategoryIds;

    @Column(name = "favorite_merchant_ids", columnDefinition = "TEXT")
    private String favoriteMerchantIds;

    @Builder.Default
    @Column(name = "average_order_value", precision = 12, scale = 2)
    private BigDecimal averageOrderValue = BigDecimal.ZERO;

    @Builder.Default
    @Column(name = "total_orders_count")
    private Integer totalOrdersCount = 0;

    @Builder.Default
    @Column(name = "engagement_score")
    private Double engagementScore = 1.0;
}
