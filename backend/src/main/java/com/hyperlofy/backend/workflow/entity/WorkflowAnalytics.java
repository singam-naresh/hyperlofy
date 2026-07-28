package com.hyperlofy.backend.workflow.entity;

import com.hyperlofy.backend.common.entity.BaseEntity;
import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.Table;
import lombok.*;
import org.hibernate.annotations.SQLDelete;
import org.hibernate.annotations.SQLRestriction;

import java.math.BigDecimal;
import java.time.LocalDate;

@Entity
@Table(name = "workflow_analytics")
@SQLDelete(sql = "UPDATE workflow_analytics SET deleted = true WHERE id = ?")
@SQLRestriction("deleted = false")
@Getter
@Setter
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class WorkflowAnalytics extends BaseEntity {

    @Column(name = "workflow_type", nullable = false, length = 80)
    private String workflowType;

    @Column(name = "period_date", nullable = false)
    private LocalDate periodDate;

    @Builder.Default
    @Column(name = "total_instances", nullable = false)
    private Integer totalInstances = 0;

    @Builder.Default
    @Column(name = "completed_instances", nullable = false)
    private Integer completedInstances = 0;

    @Builder.Default
    @Column(name = "failed_instances", nullable = false)
    private Integer failedInstances = 0;

    @Builder.Default
    @Column(name = "compensated_instances", nullable = false)
    private Integer compensatedInstances = 0;

    @Builder.Default
    @Column(name = "avg_execution_hours", nullable = false, precision = 10, scale = 2)
    private BigDecimal avgExecutionHours = BigDecimal.ZERO;

    @Builder.Default
    @Column(name = "avg_human_approval_hours", nullable = false, precision = 10, scale = 2)
    private BigDecimal avgHumanApprovalHours = BigDecimal.ZERO;

    @Builder.Default
    @Column(name = "automation_ratio", nullable = false, precision = 5, scale = 2)
    private BigDecimal automationRatio = BigDecimal.ZERO;

    @Builder.Default
    @Column(name = "sla_compliance_rate", nullable = false, precision = 5, scale = 2)
    private BigDecimal slaComplianceRate = new BigDecimal("100.00");
}
