package com.hyperlofy.backend.global.entity;

import com.hyperlofy.backend.common.entity.BaseEntity;
import jakarta.persistence.*;
import lombok.*;
import org.hibernate.annotations.SQLDelete;
import org.hibernate.annotations.SQLRestriction;

import java.time.OffsetDateTime;

@Entity
@Table(name = "disaster_recovery_plans")
@SQLDelete(sql = "UPDATE disaster_recovery_plans SET deleted = true WHERE id = ?")
@SQLRestriction("deleted = false")
@Getter
@Setter
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class DisasterRecoveryPlan extends BaseEntity {

    @Column(name = "plan_name", nullable = false, unique = true, length = 150)
    private String planName;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "primary_region_id", nullable = false)
    private GlobalRegion primaryRegion;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "target_dr_region_id", nullable = false)
    private GlobalRegion targetDrRegion;

    @Builder.Default
    @Column(name = "target_rpo_seconds", nullable = false)
    private Integer targetRpoSeconds = 5;

    @Builder.Default
    @Column(name = "target_rto_seconds", nullable = false)
    private Integer targetRtoSeconds = 60;

    @Builder.Default
    @Column(name = "status", nullable = false, length = 30)
    private String status = "READY"; // READY, EXECUTING, RECOVERED, FAILED

    @Column(name = "last_drill_at")
    private OffsetDateTime lastDrillAt;
}
