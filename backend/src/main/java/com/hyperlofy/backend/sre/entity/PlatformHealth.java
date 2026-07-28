package com.hyperlofy.backend.sre.entity;

import com.hyperlofy.backend.common.entity.BaseEntity;
import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.Table;
import lombok.*;
import org.hibernate.annotations.SQLDelete;
import org.hibernate.annotations.SQLRestriction;

import java.math.BigDecimal;
import java.time.ZonedDateTime;

@Entity
@Table(name = "platform_health")
@SQLDelete(sql = "UPDATE platform_health SET deleted = true WHERE id = ?")
@SQLRestriction("deleted = false")
@Getter
@Setter
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class PlatformHealth extends BaseEntity {

    @Column(name = "service_name", nullable = false, unique = true, length = 100)
    private String serviceName;

    @Builder.Default
    @Column(name = "health_status", nullable = false, length = 30)
    private String healthStatus = "HEALTHY"; // HEALTHY, DEGRADED, UNHEALTHY

    @Builder.Default
    @Column(name = "cpu_utilization_pct", nullable = false, precision = 5, scale = 2)
    private BigDecimal cpuUtilizationPct = new BigDecimal("15.50");

    @Builder.Default
    @Column(name = "memory_utilization_pct", nullable = false, precision = 5, scale = 2)
    private BigDecimal memoryUtilizationPct = new BigDecimal("42.10");

    @Builder.Default
    @Column(name = "p95_latency_ms", nullable = false)
    private Integer p95LatencyMs = 24;

    @Builder.Default
    @Column(name = "last_probe_at")
    private ZonedDateTime lastProbeAt = ZonedDateTime.now();
}
