package com.hyperlofy.backend.matching.entity;

import com.hyperlofy.backend.common.entity.BaseEntity;
import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.Table;
import lombok.*;
import org.hibernate.annotations.SQLDelete;
import org.hibernate.annotations.SQLRestriction;

import java.util.UUID;

@Entity
@Table(name = "matching_fairness")
@SQLDelete(sql = "UPDATE matching_fairness SET deleted = true WHERE id = ?")
@SQLRestriction("deleted = false")
@Getter
@Setter
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class MatchingFairness extends BaseEntity {

    @Column(name = "driver_id", nullable = false, unique = true)
    private UUID driverId;

    @Builder.Default
    @Column(name = "total_assignments", nullable = false)
    private Integer totalAssignments = 0;

    @Builder.Default
    @Column(name = "total_working_hours", nullable = false)
    private Double totalWorkingHours = 0.0;

    @Builder.Default
    @Column(name = "acceptance_rate", nullable = false)
    private Double acceptanceRate = 100.0;

    @Builder.Default
    @Column(name = "idle_time_minutes", nullable = false)
    private Integer idleTimeMinutes = 0;

    @Builder.Default
    @Column(name = "fairness_score", nullable = false)
    private Double fairnessScore = 95.0;
}
