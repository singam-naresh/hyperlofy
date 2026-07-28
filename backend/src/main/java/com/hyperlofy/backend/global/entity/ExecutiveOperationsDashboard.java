package com.hyperlofy.backend.global.entity;

import com.hyperlofy.backend.common.entity.BaseEntity;
import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.Table;
import lombok.*;
import org.hibernate.annotations.SQLDelete;
import org.hibernate.annotations.SQLRestriction;

import java.math.BigDecimal;

@Entity
@Table(name = "executive_operations_dashboards")
@SQLDelete(sql = "UPDATE executive_operations_dashboards SET deleted = true WHERE id = ?")
@SQLRestriction("deleted = false")
@Getter
@Setter
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class ExecutiveOperationsDashboard extends BaseEntity {

    @Column(name = "dashboard_key", nullable = false, unique = true, length = 100)
    private String dashboardKey;

    @Builder.Default
    @Column(name = "global_availability_percent", nullable = false, precision = 5, scale = 2)
    private BigDecimal globalAvailabilityPercent = new BigDecimal("99.99");

    @Builder.Default
    @Column(name = "rpo_compliance_percent", nullable = false, precision = 5, scale = 2)
    private BigDecimal rpoCompliancePercent = new BigDecimal("100.00");

    @Builder.Default
    @Column(name = "rto_compliance_percent", nullable = false, precision = 5, scale = 2)
    private BigDecimal rtoCompliancePercent = new BigDecimal("100.00");

    @Builder.Default
    @Column(name = "resilience_score", nullable = false, precision = 5, scale = 2)
    private BigDecimal resilienceScore = new BigDecimal("98.50");

    @Builder.Default
    @Column(name = "carbon_emissions_kg", nullable = false, precision = 10, scale = 2)
    private BigDecimal carbonEmissionsKg = new BigDecimal("1250.00");

    @Builder.Default
    @Column(name = "finops_savings_usd", nullable = false, precision = 16, scale = 2)
    private BigDecimal finopsSavingsUsd = new BigDecimal("18500.00");
}
