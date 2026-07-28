package com.hyperlofy.backend.pricing.entity;

import com.hyperlofy.backend.common.entity.BaseEntity;
import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.Table;
import lombok.*;
import org.hibernate.annotations.SQLDelete;
import org.hibernate.annotations.SQLRestriction;

import java.math.BigDecimal;

@Entity
@Table(name = "pricing_rules")
@SQLDelete(sql = "UPDATE pricing_rules SET deleted = true WHERE id = ?")
@SQLRestriction("deleted = false")
@Getter
@Setter
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class PricingRule extends BaseEntity {

    @Column(name = "rule_name", nullable = false, unique = true, length = 100)
    private String ruleName;

    @Column(name = "service_type", nullable = false, length = 40)
    private String serviceType;

    @Column(name = "base_fare", nullable = false, precision = 12, scale = 2)
    private BigDecimal baseFare;

    @Column(name = "min_fare", nullable = false, precision = 12, scale = 2)
    private BigDecimal minFare;

    @Column(name = "per_km_rate", nullable = false, precision = 12, scale = 2)
    private BigDecimal perKmRate;

    @Column(name = "per_minute_rate", nullable = false, precision = 12, scale = 2)
    private BigDecimal perMinuteRate;

    @Builder.Default
    @Column(name = "is_active")
    private Boolean isActive = true;
}
