package com.hyperlofy.backend.pickupdrop.entity;

import com.hyperlofy.backend.common.entity.BaseEntity;
import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.Table;
import lombok.*;
import org.hibernate.annotations.SQLDelete;
import org.hibernate.annotations.SQLRestriction;

import java.util.UUID;

@Entity
@Table(name = "pickup_drop_parcels")
@SQLDelete(sql = "UPDATE pickup_drop_parcels SET deleted = true WHERE id = ?")
@SQLRestriction("deleted = false")
@Getter
@Setter
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class PickupDropParcel extends BaseEntity {

    @Column(name = "order_id", nullable = false)
    private UUID orderId;

    @Column(name = "category", nullable = false, length = 50)
    private String category; // DOCUMENTS, KEYS, PARCELS, ELECTRONICS, CLOTHING

    @Column(name = "description", columnDefinition = "TEXT")
    private String description;

    @Builder.Default
    @Column(name = "declared_value", nullable = false)
    private Double declaredValue = 0.0;

    @Builder.Default
    @Column(name = "weight_kg", nullable = false)
    private Double weightKg = 1.0;

    @Builder.Default
    @Column(name = "is_fragile")
    private Boolean isFragile = false;

    @Builder.Default
    @Column(name = "is_liquid")
    private Boolean isLiquid = false;

    @Builder.Default
    @Column(name = "is_perishable")
    private Boolean isPerishable = false;

    @Builder.Default
    @Column(name = "is_high_value")
    private Boolean isHighValue = false;

    @Builder.Default
    @Column(name = "insurance_requested")
    private Boolean insuranceRequested = false;
}
