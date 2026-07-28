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
@Table(name = "customer_reputations")
@SQLDelete(sql = "UPDATE customer_reputations SET deleted = true WHERE id = ?")
@SQLRestriction("deleted = false")
@Getter
@Setter
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class CustomerReputation extends BaseEntity {

    @Column(name = "customer_id", nullable = false, unique = true)
    private UUID customerId;

    @Builder.Default
    @Column(name = "reputation_score", nullable = false, precision = 5, scale = 2)
    private BigDecimal reputationScore = new BigDecimal("95.00");

    @Builder.Default
    @Column(name = "verified_purchase_ratio", nullable = false, precision = 5, scale = 2)
    private BigDecimal verifiedPurchaseRatio = new BigDecimal("100.00");

    @Builder.Default
    @Column(name = "helpful_votes_received", nullable = false)
    private Integer helpfulVotesReceived = 0;

    @Builder.Default
    @Column(name = "badge_level", nullable = false, length = 50)
    private String badgeLevel = "GOLD_REVIEWER"; // BRONZE, SILVER, GOLD, ELITE, TOP_CONTRIBUTOR

    @Builder.Default
    @Column(name = "community_trust_level", nullable = false, length = 50)
    private String communityTrustLevel = "HIGHLY_TRUSTED";

    @Builder.Default
    @Column(name = "total_reviews_count", nullable = false)
    private Integer totalReviewsCount = 0;
}
