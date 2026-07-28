package com.hyperlofy.backend.pricing.entity;

import com.hyperlofy.backend.common.entity.BaseEntity;
import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.Table;
import lombok.*;
import org.hibernate.annotations.SQLDelete;
import org.hibernate.annotations.SQLRestriction;

import java.util.UUID;

@Entity
@Table(name = "pricing_coupons")
@SQLDelete(sql = "UPDATE pricing_coupons SET deleted = true WHERE id = ?")
@SQLRestriction("deleted = false")
@Getter
@Setter
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class PricingCoupon extends BaseEntity {

    @Column(name = "coupon_code", nullable = false, unique = true, length = 50)
    private String couponCode;

    @Column(name = "promotion_id", nullable = false)
    private UUID promotionId;

    @Builder.Default
    @Column(name = "max_redemptions")
    private Integer maxRedemptions = 1000;

    @Builder.Default
    @Column(name = "current_redemptions")
    private Integer currentRedemptions = 0;

    @Builder.Default
    @Column(name = "is_active")
    private Boolean isActive = true;
}
