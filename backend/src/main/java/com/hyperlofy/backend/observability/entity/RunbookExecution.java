package com.hyperlofy.backend.observability.entity;

import com.hyperlofy.backend.common.entity.BaseEntity;
import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.Table;
import lombok.*;
import org.hibernate.annotations.SQLDelete;
import org.hibernate.annotations.SQLRestriction;

@Entity
@Table(name = "runbook_executions")
@SQLDelete(sql = "UPDATE runbook_executions SET deleted = true WHERE id = ?")
@SQLRestriction("deleted = false")
@Getter
@Setter
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class RunbookExecution extends BaseEntity {

    @Column(name = "runbook_name", nullable = false, length = 150)
    private String runbookName;

    @Column(name = "target_service", nullable = false, length = 100)
    private String targetService;

    @Column(name = "action_type", nullable = false, length = 50)
    private String actionType; // POD_RESTART, TRAFFIC_SHIFT, CAPACITY_SCALE

    @Builder.Default
    @Column(name = "execution_status", nullable = false, length = 30)
    private String executionStatus = "SUCCESS"; // SUCCESS, FAILED, IN_PROGRESS

    @Builder.Default
    @Column(name = "execution_time_ms", nullable = false)
    private Long executionTimeMs = 1200L;
}
