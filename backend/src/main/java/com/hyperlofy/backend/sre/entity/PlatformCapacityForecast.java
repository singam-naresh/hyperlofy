package com.hyperlofy.backend.sre.entity;

import com.hyperlofy.backend.common.entity.BaseEntity;
import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.Table;
import lombok.*;
import org.hibernate.annotations.SQLDelete;
import org.hibernate.annotations.SQLRestriction;

import java.math.BigDecimal;

@Entity
@Table(name = "platform_capacity_forecasts")
@SQLDelete(sql = "UPDATE platform_capacity_forecasts SET deleted = true WHERE id = ?")
@SQLRestriction("deleted = false")
@Getter
@Setter
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class PlatformCapacityForecast extends BaseEntity {

    @Column(name = "cluster_name", nullable = false, length = 100)
    private String clusterName;

    @Column(name = "resource_type", nullable = false, length = 50)
    private String resourceType; // CPU, MEMORY, STORAGE, POD_COUNT

    @Builder.Default
    @Column(name = "current_utilization_pct", nullable = false, precision = 5, scale = 2)
    private BigDecimal currentUtilizationPct = new BigDecimal("45.00");

    @Builder.Default
    @Column(name = "forecasted_utilization_pct", nullable = false, precision = 5, scale = 2)
    private BigDecimal forecastedUtilizationPct = new BigDecimal("78.50");

    @Builder.Default
    @Column(name = "recommended_node_count", nullable = false)
    private Integer recommendedNodeCount = 24;
}
