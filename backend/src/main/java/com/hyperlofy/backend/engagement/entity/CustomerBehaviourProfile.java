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
@Table(name = "customer_behaviour_profiles")
@SQLDelete(sql = "UPDATE customer_behaviour_profiles SET deleted = true WHERE id = ?")
@SQLRestriction("deleted = false")
@Getter
@Setter
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class CustomerBehaviourProfile extends BaseEntity {

    @Column(name = "customer_id", nullable = false, unique = true)
    private UUID customerId;

    @Builder.Default
    @Column(name = "engagement_score", nullable = false, precision = 5, scale = 2)
    private BigDecimal engagementScore = new BigDecimal("85.50");

    @Builder.Default
    @Column(name = "customer_lifetime_value", nullable = false, precision = 16, scale = 2)
    private BigDecimal customerLifetimeValue = new BigDecimal("12500.00");

    @Builder.Default
    @Column(name = "purchase_frequency_days", nullable = false, precision = 5, scale = 2)
    private BigDecimal purchaseFrequencyDays = new BigDecimal("7.50");

    @Builder.Default
    @Column(name = "preferred_categories", length = 500)
    private String preferredCategories = "GROCERY,RESTAURANT";

    @Column(name = "favorite_merchant_ids", length = 1000)
    private String favoriteMerchantIds;

    @Builder.Default
    @Column(name = "churn_probability", nullable = false, precision = 5, scale = 4)
    private BigDecimal churnProbability = new BigDecimal("0.0500");

    @Column(name = "tenant_id")
    private UUID tenantId;
}
