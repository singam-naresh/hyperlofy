package com.hyperlofy.backend.ai.verify;

import com.hyperlofy.backend.common.entity.BaseEntity;
import com.hyperlofy.backend.order.entity.Order;
import com.hyperlofy.backend.user.entity.User;
import jakarta.persistence.*;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

import java.time.OffsetDateTime;
import java.util.UUID;

@Entity
@Table(name = "verifications", indexes = {
        @Index(name = "idx_verifications_order", columnList = "order_id"),
        @Index(name = "idx_verifications_type", columnList = "verification_type"),
        @Index(name = "idx_verifications_result", columnList = "verification_result")
})
@Getter
@Setter
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class VerifyEntity extends BaseEntity {

    @Column(name = "verification_id", nullable = false, unique = true, updatable = false)
    private UUID verificationId;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "order_id", nullable = false)
    private Order order;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "created_by_user_id", nullable = false)
    private User createdByUser;

    @Enumerated(EnumType.STRING)
    @Column(name = "verification_type", nullable = false, length = 50)
    private VerificationType verificationType;

    @Enumerated(EnumType.STRING)
    @Column(name = "verification_result", nullable = false, length = 30)
    private VerificationResult verificationResult;

    @Column(name = "payload", nullable = false, columnDefinition = "TEXT")
    private String payload;

    @Column(name = "expected_value", length = 500)
    private String expectedValue;

    @Column(name = "expected_price", precision = 10, scale = 2)
    private Double expectedPrice;

    @Column(name = "source_url", length = 400)
    private String sourceUrl;

    @Column(name = "score", nullable = false)
    private double score;

    @Column(name = "message", length = 500)
    private String message;

    @Column(name = "details", columnDefinition = "TEXT")
    private String details;

    @Column(name = "processed_at")
    private OffsetDateTime processedAt;

    @Column(name = "active", nullable = false)
    private boolean active = true;
}
