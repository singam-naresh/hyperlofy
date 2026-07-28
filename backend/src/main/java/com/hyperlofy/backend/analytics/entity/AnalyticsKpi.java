package com.hyperlofy.backend.analytics.entity;

import com.hyperlofy.backend.common.entity.BaseEntity;
import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.Table;
import lombok.*;
import org.hibernate.annotations.SQLDelete;
import org.hibernate.annotations.SQLRestriction;

import java.math.BigDecimal;

@Entity
@Table(name = "analytics_kpis")
@SQLDelete(sql = "UPDATE analytics_kpis SET deleted = true WHERE id = ?")
@SQLRestriction("deleted = false")
@Getter
@Setter
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class AnalyticsKpi extends BaseEntity {

    @Column(name = "kpi_code", nullable = false, unique = true, length = 100)
    private String kpiCode;

    @Column(name = "kpi_name", nullable = false, length = 150)
    private String kpiName;

    @Builder.Default
    @Column(name = "metric_value", nullable = false, precision = 16, scale = 4)
    private BigDecimal metricValue = BigDecimal.ZERO;

    @Builder.Default
    @Column(name = "unit", nullable = false, length = 30)
    private String unit = "INR"; // INR, PERCENTAGE, SECONDS, COUNT

    @Builder.Default
    @Column(name = "period_code", nullable = false, length = 30)
    private String periodCode = "REALTIME";
}
