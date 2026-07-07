package com.hyperlofy.backend.zone.entity;

import com.hyperlofy.backend.common.entity.BaseEntity;
import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.FetchType;
import jakarta.persistence.JoinColumn;
import jakarta.persistence.ManyToOne;
import jakarta.persistence.Table;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;
import org.hibernate.annotations.SQLDelete;
import org.hibernate.annotations.SQLRestriction;

import java.math.BigDecimal;

@Entity
@Table(name = "pricing_slabs")
@SQLDelete(sql = "UPDATE pricing_slabs SET deleted = true WHERE id = ?")
@SQLRestriction("deleted = false")
@Getter
@Setter
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class PricingSlab extends BaseEntity {

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "zone_id", nullable = false)
    private Zone zone;

    @Column(name = "min_distance_km", nullable = false)
    private double minDistanceKm;

    @Column(name = "max_distance_km", nullable = false)
    private double maxDistanceKm;

    @Column(name = "base_price", nullable = false, precision = 10, scale = 2)
    private BigDecimal basePrice;

    @Column(name = "per_km_price", nullable = false, precision = 10, scale = 2)
    private BigDecimal perKmPrice;
}
