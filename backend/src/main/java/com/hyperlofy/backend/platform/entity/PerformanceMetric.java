package com.hyperlofy.backend.platform.entity;

import com.hyperlofy.backend.common.entity.BaseEntity;
import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.Table;
import lombok.*;
import org.hibernate.annotations.SQLDelete;
import org.hibernate.annotations.SQLRestriction;

@Entity
@Table(name = "performance_metrics")
@SQLDelete(sql = "UPDATE performance_metrics SET deleted = true WHERE id = ?")
@SQLRestriction("deleted = false")
@Getter
@Setter
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class PerformanceMetric extends BaseEntity {

    @Column(name = "service_name", nullable = false, length = 100)
    private String serviceName;

    @Builder.Default
    @Column(name = "p95_latency_ms", nullable = false)
    private Double p95LatencyMs = 45.0;

    @Builder.Default
    @Column(name = "p99_latency_ms", nullable = false)
    private Double p99LatencyMs = 120.0;

    @Builder.Default
    @Column(name = "cpu_utilization_percentage", nullable = false)
    private Double cpuUtilizationPercentage = 35.0;

    @Builder.Default
    @Column(name = "memory_utilization_percentage", nullable = false)
    private Double memoryUtilizationPercentage = 42.0;

    @Builder.Default
    @Column(name = "cache_hit_ratio", nullable = false)
    private Double cacheHitRatio = 96.5;

    @Builder.Default
    @Column(name = "error_rate_percentage", nullable = false)
    private Double errorRatePercentage = 0.01;
}
