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
@Table(name = "analytics_anomalies")
@SQLDelete(sql = "UPDATE analytics_anomalies SET deleted = true WHERE id = ?")
@SQLRestriction("deleted = false")
@Getter
@Setter
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class AnalyticsAnomaly extends BaseEntity {

    @Column(name = "metric_code", nullable = false, length = 100)
    private String metricCode;

    @Column(name = "anomaly_type", nullable = false, length = 50)
    private String anomalyType; // REVENUE_DROP, REFUND_SPIKE, SETTLEMENT_FAILURE, SLA_DEGRADATION

    @Builder.Default
    @Column(name = "severity", nullable = false, length = 30)
    private String severity = "MEDIUM";

    @Builder.Default
    @Column(name = "baseline_value", nullable = false, precision = 16, scale = 4)
    private BigDecimal baselineValue = BigDecimal.ZERO;

    @Builder.Default
    @Column(name = "observed_value", nullable = false, precision = 16, scale = 4)
    private BigDecimal observedValue = BigDecimal.ZERO;

    @Builder.Default
    @Column(name = "status", nullable = false, length = 30)
    private String status = "OPEN";
}
