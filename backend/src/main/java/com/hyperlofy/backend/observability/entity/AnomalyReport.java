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
@Table(name = "anomaly_reports")
@SQLDelete(sql = "UPDATE anomaly_reports SET deleted = true WHERE id = ?")
@SQLRestriction("deleted = false")
@Getter
@Setter
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class AnomalyReport extends BaseEntity {

    @Column(name = "service_name", nullable = false, length = 100)
    private String serviceName;

    @Column(name = "anomaly_type", nullable = false, length = 50)
    private String anomalyType; // LATENCY_SPIKE, ERROR_BURST, MEMORY_LEAK

    @Builder.Default
    @Column(name = "severity", nullable = false, length = 30)
    private String severity = "CRITICAL"; // CRITICAL, HIGH, MEDIUM

    @Builder.Default
    @Column(name = "confidence_score", nullable = false, precision = 5, scale = 2)
    private BigDecimal confidenceScore = new BigDecimal("98.50");

    @Builder.Default
    @Column(name = "status", nullable = false, length = 30)
    private String status = "DETECTED"; // DETECTED, INVESTIGATING, RESOLVED
}
