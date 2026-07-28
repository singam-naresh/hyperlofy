package com.hyperlofy.backend.workflow.entity;

import com.hyperlofy.backend.common.entity.BaseEntity;
import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.Table;
import lombok.*;
import org.hibernate.annotations.SQLDelete;
import org.hibernate.annotations.SQLRestriction;

@Entity
@Table(name = "workflow_definitions")
@SQLDelete(sql = "UPDATE workflow_definitions SET deleted = true WHERE id = ?")
@SQLRestriction("deleted = false")
@Getter
@Setter
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class WorkflowDefinition extends BaseEntity {

    @Column(name = "workflow_key", nullable = false, unique = true, length = 100)
    private String workflowKey;

    @Column(name = "workflow_name", nullable = false, length = 150)
    private String workflowName;

    /**
     * Workflow process type — e.g. MERCHANT_REGISTRATION, KYC_APPROVAL, REFUND_APPROVAL,
     * FRAUD_INVESTIGATION, ORDER_EXCEPTION, COMPLIANCE_REVIEW, WALLET_ADJUSTMENT, etc.
     */
    @Column(name = "workflow_type", nullable = false, length = 80)
    private String workflowType;

    @Builder.Default
    @Column(name = "version", nullable = false)
    private Integer version = 1;

    @Column(name = "description", columnDefinition = "TEXT")
    private String description;

    @Builder.Default
    @Column(name = "is_active", nullable = false)
    private Boolean isActive = true;

    @Builder.Default
    @Column(name = "timeout_hours", nullable = false)
    private Integer timeoutHours = 72;

    @Builder.Default
    @Column(name = "retry_limit", nullable = false)
    private Integer retryLimit = 3;
}
