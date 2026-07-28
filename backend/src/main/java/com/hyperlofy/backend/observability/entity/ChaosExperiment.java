package com.hyperlofy.backend.observability.entity;

import com.hyperlofy.backend.common.entity.BaseEntity;
import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.Table;
import lombok.*;
import org.hibernate.annotations.SQLDelete;
import org.hibernate.annotations.SQLRestriction;

import java.math.BigDecimal;
import java.time.OffsetDateTime;

@Entity
@Table(name = "chaos_experiments")
@SQLDelete(sql = "UPDATE chaos_experiments SET deleted = true WHERE id = ?")
@SQLRestriction("deleted = false")
@Getter
@Setter
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class ChaosExperiment extends BaseEntity {

    @Column(name = "experiment_name", nullable = false, unique = true, length = 150)
    private String experimentName;

    @Column(name = "experiment_type", nullable = false, length = 50)
    private String experimentType; // POD_KILL, NETWORK_LATENCY, DB_FAILOVER

    @Column(name = "target_service", nullable = false, length = 100)
    private String targetService;

    @Builder.Default
    @Column(name = "status", nullable = false, length = 30)
    private String status = "COMPLETED"; // SCHEDULED, RUNNING, COMPLETED, ABORTED

    @Builder.Default
    @Column(name = "resilience_score", nullable = false, precision = 5, scale = 2)
    private BigDecimal resilienceScore = new BigDecimal("98.00");

    @Builder.Default
    @Column(name = "started_at")
    private OffsetDateTime startedAt = OffsetDateTime.now();

    @Builder.Default
    @Column(name = "completed_at")
    private OffsetDateTime completedAt = OffsetDateTime.now();
}
