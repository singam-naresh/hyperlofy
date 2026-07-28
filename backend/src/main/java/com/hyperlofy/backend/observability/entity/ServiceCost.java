package com.hyperlofy.backend.observability.entity;

import com.hyperlofy.backend.common.entity.BaseEntity;
import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.Table;
import lombok.*;
import org.hibernate.annotations.SQLDelete;
import org.hibernate.annotations.SQLRestriction;

import java.math.BigDecimal;

@Entity
@Table(name = "service_costs")
@SQLDelete(sql = "UPDATE service_costs SET deleted = true WHERE id = ?")
@SQLRestriction("deleted = false")
@Getter
@Setter
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class ServiceCost extends BaseEntity {

    @Column(name = "service_name", nullable = false, unique = true, length = 100)
    private String serviceName;

    @Builder.Default
    @Column(name = "monthly_cost_usd", nullable = false, precision = 16, scale = 2)
    private BigDecimal monthlyCostUsd = new BigDecimal("1500.00");

    @Builder.Default
    @Column(name = "compute_cost", nullable = false, precision = 16, scale = 2)
    private BigDecimal computeCost = new BigDecimal("900.00");

    @Builder.Default
    @Column(name = "storage_cost", nullable = false, precision = 16, scale = 2)
    private BigDecimal storageCost = new BigDecimal("400.00");

    @Builder.Default
    @Column(name = "network_cost", nullable = false, precision = 16, scale = 2)
    private BigDecimal networkCost = new BigDecimal("200.00");

    @Builder.Default
    @Column(name = "cost_status", nullable = false, length = 30)
    private String costStatus = "OPTIMIZED"; // OPTIMIZED, OVER_BUDGET, ANOMALY
}
