package com.hyperlofy.backend.buyforme.entity;

import com.hyperlofy.backend.common.entity.BaseEntity;
import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.Table;
import lombok.*;
import org.hibernate.annotations.SQLDelete;
import org.hibernate.annotations.SQLRestriction;

import java.util.UUID;

@Entity
@Table(name = "buy_for_me_price_breakdown")
@SQLDelete(sql = "UPDATE buy_for_me_price_breakdown SET deleted = true WHERE id = ?")
@SQLRestriction("deleted = false")
@Getter
@Setter
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class BuyForMePriceBreakdown extends BaseEntity {

    @Column(name = "order_id", nullable = false)
    private UUID orderId;

    @Builder.Default
    @Column(name = "product_cost", nullable = false)
    private Double productCost = 0.0;

    @Builder.Default
    @Column(name = "delivery_fee", nullable = false)
    private Double deliveryFee = 0.0;

    @Builder.Default
    @Column(name = "service_fee", nullable = false)
    private Double serviceFee = 0.0;

    @Builder.Default
    @Column(name = "platform_fee", nullable = false)
    private Double platformFee = 0.0;

    @Builder.Default
    @Column(name = "tax", nullable = false)
    private Double tax = 0.0;

    @Builder.Default
    @Column(name = "surge_pricing")
    private Double surgePricing = 0.0;

    @Builder.Default
    @Column(name = "rain_surcharge")
    private Double rainSurcharge = 0.0;

    @Builder.Default
    @Column(name = "distance_charges")
    private Double distanceCharges = 0.0;

    @Builder.Default
    @Column(name = "shopping_charges")
    private Double shoppingCharges = 0.0;

    @Column(name = "total_payable", nullable = false)
    private Double totalPayable;
}
