package com.hyperlofy.backend.pricing.entity;

import com.hyperlofy.backend.common.entity.BaseEntity;
import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.Table;
import lombok.*;
import org.hibernate.annotations.SQLDelete;
import org.hibernate.annotations.SQLRestriction;

import java.math.BigDecimal;
import java.time.ZonedDateTime;
import java.util.UUID;

@Entity
@Table(name = "pricing_quotes")
@SQLDelete(sql = "UPDATE pricing_quotes SET deleted = true WHERE id = ?")
@SQLRestriction("deleted = false")
@Getter
@Setter
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class PricingQuote extends BaseEntity {

    @Column(name = "order_id")
    private UUID orderId;

    @Column(name = "service_type", nullable = false, length = 40)
    private String serviceType;

    @Builder.Default
    @Column(name = "service_level", nullable = false, length = 30)
    private String serviceLevel = "STANDARD";

    @Column(name = "base_fare", nullable = false, precision = 12, scale = 2)
    private BigDecimal baseFare;

    @Column(name = "distance_charge", nullable = false, precision = 12, scale = 2)
    private BigDecimal distanceCharge;

    @Column(name = "time_charge", nullable = false, precision = 12, scale = 2)
    private BigDecimal timeCharge;

    @Builder.Default
    @Column(name = "surge_multiplier")
    private Double surgeMultiplier = 1.0;

    @Builder.Default
    @Column(name = "service_fee", precision = 12, scale = 2)
    private BigDecimal serviceFee = BigDecimal.ZERO;

    @Builder.Default
    @Column(name = "platform_fee", precision = 12, scale = 2)
    private BigDecimal platformFee = BigDecimal.ZERO;

    @Builder.Default
    @Column(name = "tax_amount", precision = 12, scale = 2)
    private BigDecimal taxAmount = BigDecimal.ZERO;

    @Column(name = "total_amount", nullable = false, precision = 12, scale = 2)
    private BigDecimal totalAmount;

    @Builder.Default
    @Column(name = "status", nullable = false, length = 30)
    private String status = "QUOTE_CREATED";

    @Column(name = "expires_at", nullable = false)
    private ZonedDateTime expiresAt;
}
