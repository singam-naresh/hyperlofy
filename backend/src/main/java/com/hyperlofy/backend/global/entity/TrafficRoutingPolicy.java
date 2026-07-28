package com.hyperlofy.backend.global.entity;

import com.hyperlofy.backend.common.entity.BaseEntity;
import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.Table;
import lombok.*;
import org.hibernate.annotations.SQLDelete;
import org.hibernate.annotations.SQLRestriction;

@Entity
@Table(name = "traffic_routing_policies")
@SQLDelete(sql = "UPDATE traffic_routing_policies SET deleted = true WHERE id = ?")
@SQLRestriction("deleted = false")
@Getter
@Setter
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class TrafficRoutingPolicy extends BaseEntity {

    @Column(name = "policy_name", nullable = false, unique = true, length = 150)
    private String policyName;

    @Builder.Default
    @Column(name = "routing_type", nullable = false, length = 50)
    private String routingType = "GEO_LATENCY"; // GEO_LATENCY, WEIGHTED, FAILOVER, HEALTH_BASED

    @Column(name = "target_region_code", nullable = false, length = 50)
    private String targetRegionCode;

    @Builder.Default
    @Column(name = "traffic_weight_percent", nullable = false)
    private Integer trafficWeightPercent = 100;

    @Builder.Default
    @Column(name = "health_status", nullable = false, length = 30)
    private String healthStatus = "HEALTHY"; // HEALTHY, DEGRADED, DRAINED
}
