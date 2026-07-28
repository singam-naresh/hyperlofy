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
@Table(name = "customer_reviews")
@SQLDelete(sql = "UPDATE customer_reviews SET deleted = true WHERE id = ?")
@SQLRestriction("deleted = false")
@Getter
@Setter
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class CustomerReview extends BaseEntity {

    @Column(name = "review_code", nullable = false, unique = true, length = 100)
    private String reviewCode;

    @Column(name = "customer_id", nullable = false)
    private UUID customerId;

    @Column(name = "product_id")
    private UUID productId;

    @Column(name = "merchant_id")
    private UUID merchantId;

    @Column(name = "delivery_partner_id")
    private UUID deliveryPartnerId;

    @Column(name = "order_id")
    private UUID orderId;

    @Column(name = "title", nullable = false, length = 255)
    private String title;

    @Column(name = "content", nullable = false, columnDefinition = "TEXT")
    private String content;

    @Builder.Default
    @Column(name = "rating", nullable = false, precision = 3, scale = 2)
    private BigDecimal rating = new BigDecimal("5.00");

    @Builder.Default
    @Column(name = "is_verified_purchase", nullable = false)
    private Boolean isVerifiedPurchase = true;

    @Builder.Default
    @Column(name = "status", nullable = false, length = 30)
    private String status = "APPROVED"; // PENDING_MODERATION, APPROVED, REJECTED, FLAGGED

    @Builder.Default
    @Column(name = "ai_trust_score", nullable = false, precision = 5, scale = 2)
    private BigDecimal aiTrustScore = new BigDecimal("98.50");

    @Builder.Default
    @Column(name = "helpful_count", nullable = false)
    private Integer helpfulCount = 0;

    @Column(name = "media_urls", length = 1000)
    private String mediaUrls;

    @Column(name = "tenant_id")
    private UUID tenantId;
}
