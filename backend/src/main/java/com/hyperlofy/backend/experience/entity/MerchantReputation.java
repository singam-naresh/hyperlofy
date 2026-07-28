package com.hyperlofy.backend.experience.entity;

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
@Table(name = "merchant_reputations")
@SQLDelete(sql = "UPDATE merchant_reputations SET deleted = true WHERE id = ?")
@SQLRestriction("deleted = false")
@Getter
@Setter
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class MerchantReputation extends BaseEntity {

    @Column(name = "merchant_id", nullable = false, unique = true)
    private UUID merchantId;

    @Builder.Default
    @Column(name = "average_rating", nullable = false, precision = 3, scale = 2)
    private BigDecimal averageRating = new BigDecimal("4.85");

    @Builder.Default
    @Column(name = "total_reviews_count", nullable = false)
    private Integer totalReviewsCount = 0;

    @Builder.Default
    @Column(name = "csat_score_percent", nullable = false, precision = 5, scale = 2)
    private BigDecimal csatScorePercent = new BigDecimal("96.50");

    @Builder.Default
    @Column(name = "avg_response_time_hours", nullable = false, precision = 5, scale = 2)
    private BigDecimal avgResponseTimeHours = new BigDecimal("2.40");

    @Builder.Default
    @Column(name = "complaint_ratio_percent", nullable = false, precision = 5, scale = 2)
    private BigDecimal complaintRatioPercent = new BigDecimal("0.80");

    @Builder.Default
    @Column(name = "ai_trust_score", nullable = false, precision = 5, scale = 2)
    private BigDecimal aiTrustScore = new BigDecimal("99.00");
}
