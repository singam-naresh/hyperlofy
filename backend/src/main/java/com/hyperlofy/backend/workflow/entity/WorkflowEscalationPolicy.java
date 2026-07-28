package com.hyperlofy.backend.workflow.entity;

import com.hyperlofy.backend.common.entity.BaseEntity;
import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.Table;
import lombok.*;
import org.hibernate.annotations.SQLDelete;
import org.hibernate.annotations.SQLRestriction;

@Entity
@Table(name = "workflow_escalation_policies")
@SQLDelete(sql = "UPDATE workflow_escalation_policies SET deleted = true WHERE id = ?")
@SQLRestriction("deleted = false")
@Getter
@Setter
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class WorkflowEscalationPolicy extends BaseEntity {

    @Column(name = "policy_name", nullable = false, unique = true, length = 150)
    private String policyName;

    @Column(name = "applies_to_workflow_type", length = 80)
    private String appliesToWorkflowType; // NULL means applies to all types

    @Builder.Default
    @Column(name = "warning_hours", nullable = false)
    private Integer warningHours = 24;

    @Builder.Default
    @Column(name = "breach_hours", nullable = false)
    private Integer breachHours = 48;

    @Column(name = "escalation_level_1_group", nullable = false, length = 100)
    private String escalationLevel1Group;

    @Column(name = "escalation_level_2_group", length = 100)
    private String escalationLevel2Group;

    @Column(name = "escalation_level_3_group", length = 100)
    private String escalationLevel3Group;

    @Column(name = "auto_cancel_hours")
    private Integer autoCancelHours;

    @Builder.Default
    @Column(name = "is_active", nullable = false)
    private Boolean isActive = true;
}
