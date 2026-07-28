package com.hyperlofy.backend.global.entity;

import com.hyperlofy.backend.common.entity.BaseEntity;
import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.Table;
import lombok.*;
import org.hibernate.annotations.SQLDelete;
import org.hibernate.annotations.SQLRestriction;

@Entity
@Table(name = "global_traffic_optimizations")
@SQLDelete(sql = "UPDATE global_traffic_optimizations SET deleted = true WHERE id = ?")
@SQLRestriction("deleted = false")
@Getter
@Setter
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class GlobalTrafficOptimization extends BaseEntity {

    @Column(name = "optimization_code", nullable = false, unique = true, length = 100)
    private String optimizationCode;

    @Column(name = "source_region_code", nullable = false, length = 50)
    private String sourceRegionCode;

    @Column(name = "target_region_code", nullable = false, length = 50)
    private String targetRegionCode;

    @Builder.Default
    @Column(name = "shifted_traffic_percent", nullable = false)
    private Integer shiftedTrafficPercent = 20;

    @Column(name = "optimization_reason", nullable = false, length = 150)
    private String optimizationReason; // LATENCY_SPIKE, COST_SAVING, CARBON_REDUCTION, HEALTH_DEGRADATION

    @Builder.Default
    @Column(name = "latency_reduction_ms", nullable = false)
    private Integer latencyReductionMs = 45;

    @Builder.Default
    @Column(name = "status", nullable = false, length = 30)
    private String status = "ACTIVE"; // ACTIVE, REVERTED
}
