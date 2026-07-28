package com.hyperlofy.backend.workflow.entity;

import com.hyperlofy.backend.common.entity.BaseEntity;
import jakarta.persistence.*;
import lombok.*;
import org.hibernate.annotations.SQLDelete;
import org.hibernate.annotations.SQLRestriction;

import java.time.OffsetDateTime;
import java.util.UUID;

@Entity
@Table(name = "workflow_cases")
@SQLDelete(sql = "UPDATE workflow_cases SET deleted = true WHERE id = ?")
@SQLRestriction("deleted = false")
@Getter
@Setter
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class WorkflowCase extends BaseEntity {

    @Column(name = "case_ref", nullable = false, unique = true, length = 100)
    private String caseRef;

    /**
     * Case types: FRAUD_INVESTIGATION, COMPLIANCE_INVESTIGATION, CUSTOMER_COMPLAINT,
     * CHARGEBACK_REVIEW, LEGAL_REVIEW, MERCHANT_SUSPENSION, DELIVERY_PARTNER_APPEAL, RISK_ASSESSMENT
     */
    @Column(name = "case_type", nullable = false, length = 80)
    private String caseType;

    @Column(name = "title", nullable = false, length = 200)
    private String title;

    @Column(name = "description", columnDefinition = "TEXT")
    private String description;

    /**
     * Case status: OPEN, IN_PROGRESS, ESCALATED, SUSPENDED, CLOSED, ARCHIVED
     */
    @Builder.Default
    @Column(name = "status", nullable = false, length = 50)
    private String status = "OPEN";

    @Builder.Default
    @Column(name = "priority", nullable = false, length = 20)
    private String priority = "NORMAL";

    @Column(name = "subject_user_id")
    private UUID subjectUserId;

    @Column(name = "assignee_user_id")
    private UUID assigneeUserId;

    @Column(name = "tenant_id")
    private UUID tenantId;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "related_workflow_instance_id")
    private WorkflowInstance relatedWorkflowInstance;

    @Column(name = "due_at")
    private OffsetDateTime dueAt;

    @Column(name = "closed_at")
    private OffsetDateTime closedAt;

    @Column(name = "resolution", columnDefinition = "TEXT")
    private String resolution;
}
