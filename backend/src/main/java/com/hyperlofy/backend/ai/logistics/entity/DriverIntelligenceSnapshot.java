package com.hyperlofy.backend.ai.logistics.entity;

import com.hyperlofy.backend.common.entity.BaseEntity;
import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.Table;
import lombok.*;
import org.hibernate.annotations.SQLDelete;
import org.hibernate.annotations.SQLRestriction;

import java.util.UUID;

@Entity
@Table(name = "driver_intelligence_snapshots")
@SQLDelete(sql = "UPDATE driver_intelligence_snapshots SET deleted = true WHERE id = ?")
@SQLRestriction("deleted = false")
@Getter
@Setter
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class DriverIntelligenceSnapshot extends BaseEntity {

    @Column(name = "driver_id", nullable = false, unique = true)
    private UUID driverId;

    @Builder.Default
    @Column(name = "acceptance_rate")
    private Double acceptanceRate = 1.0;

    @Builder.Default
    @Column(name = "completion_rate")
    private Double completionRate = 1.0;

    @Builder.Default
    @Column(name = "average_speed_kmh")
    private Double averageSpeedKmh = 25.0;

    @Builder.Default
    @Column(name = "reliability_score")
    private Double reliabilityScore = 0.95;

    @Builder.Default
    @Column(name = "efficiency_score")
    private Double efficiencyScore = 0.92;

    @Builder.Default
    @Column(name = "rating")
    private Double rating = 4.8;
}
