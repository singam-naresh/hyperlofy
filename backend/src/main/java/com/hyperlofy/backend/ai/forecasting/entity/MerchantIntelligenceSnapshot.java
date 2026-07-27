package com.hyperlofy.backend.ai.forecasting.entity;

import com.hyperlofy.backend.common.entity.BaseEntity;
import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.Table;
import lombok.*;
import org.hibernate.annotations.SQLDelete;
import org.hibernate.annotations.SQLRestriction;

import java.util.UUID;

@Entity
@Table(name = "merchant_intelligence_snapshots")
@SQLDelete(sql = "UPDATE merchant_intelligence_snapshots SET deleted = true WHERE id = ?")
@SQLRestriction("deleted = false")
@Getter
@Setter
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class MerchantIntelligenceSnapshot extends BaseEntity {

    @Column(name = "merchant_id", nullable = false, unique = true)
    private UUID merchantId;

    @Builder.Default
    @Column(name = "growth_score")
    private Double growthScore = 1.0;

    @Builder.Default
    @Column(name = "health_score")
    private Double healthScore = 1.0;

    @Builder.Default
    @Column(name = "repeat_customer_rate")
    private Double repeatCustomerRate = 0.0;

    @Column(name = "peak_ordering_hours", length = 100)
    private String peakOrderingHours;

    @Column(name = "top_selling_product_ids", columnDefinition = "TEXT")
    private String topSellingProductIds;

    @Builder.Default
    @Column(name = "low_stock_product_count")
    private Integer lowStockProductCount = 0;
}
