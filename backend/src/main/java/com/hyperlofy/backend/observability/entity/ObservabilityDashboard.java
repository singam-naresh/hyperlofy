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
@Table(name = "observability_dashboards")
@SQLDelete(sql = "UPDATE observability_dashboards SET deleted = true WHERE id = ?")
@SQLRestriction("deleted = false")
@Getter
@Setter
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class ObservabilityDashboard extends BaseEntity {

    @Column(name = "dashboard_name", nullable = false, unique = true, length = 150)
    private String dashboardName;

    @Builder.Default
    @Column(name = "category", nullable = false, length = 50)
    private String category = "EXECUTIVE_OPS"; // EXECUTIVE_OPS, FINOPS, SLO_COMPLIANCE

    @Column(name = "grafana_url", nullable = false, length = 255)
    private String grafanaUrl;

    @Builder.Default
    @Column(name = "slo_percentage", nullable = false, precision = 5, scale = 2)
    private BigDecimal sloPercentage = new BigDecimal("99.99");

    @Builder.Default
    @Column(name = "error_budget_remaining", nullable = false, precision = 5, scale = 2)
    private BigDecimal errorBudgetRemaining = new BigDecimal("95.00");
}
