package com.hyperlofy.backend.global.entity;

import com.hyperlofy.backend.common.entity.BaseEntity;
import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.Table;
import lombok.*;
import org.hibernate.annotations.SQLDelete;
import org.hibernate.annotations.SQLRestriction;

@Entity
@Table(name = "autonomous_recovery_executions")
@SQLDelete(sql = "UPDATE autonomous_recovery_executions SET deleted = true WHERE id = ?")
@SQLRestriction("deleted = false")
@Getter
@Setter
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class AutonomousRecoveryExecution extends BaseEntity {

    @Column(name = "execution_code", nullable = false, unique = true, length = 100)
    private String executionCode;

    @Column(name = "target_service", nullable = false, length = 100)
    private String targetService;

    @Column(name = "region_code", nullable = false, length = 50)
    private String regionCode;

    @Column(name = "action_type", nullable = false, length = 80)
    private String actionType; // RESTART_POD, REPLACE_NODE, REDIS_FAILOVER, CERT_RENEW, SECRET_ROTATE

    @Column(name = "trigger_reason", nullable = false, columnDefinition = "TEXT")
    private String triggerReason;

    @Builder.Default
    @Column(name = "status", nullable = false, length = 30)
    private String status = "COMPLETED"; // IN_PROGRESS, COMPLETED, FAILED

    @Builder.Default
    @Column(name = "execution_duration_ms", nullable = false)
    private Long executionDurationMs = 1200L;
}
