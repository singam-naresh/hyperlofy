package com.hyperlofy.backend.workflow.entity;

import com.hyperlofy.backend.common.entity.BaseEntity;
import jakarta.persistence.*;
import lombok.*;
import org.hibernate.annotations.SQLDelete;
import org.hibernate.annotations.SQLRestriction;

import java.time.OffsetDateTime;
import java.util.UUID;

@Entity
@Table(name = "workflow_instances")
@SQLDelete(sql = "UPDATE workflow_instances SET deleted = true WHERE id = ?")
@SQLRestriction("deleted = false")
@Getter
@Setter
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class WorkflowInstance extends BaseEntity {

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "definition_id", nullable = false)
    private WorkflowDefinition definition;

    @Column(name = "instance_ref", nullable = false, unique = true, length = 100)
    private String instanceRef;

    @Column(name = "tenant_id")
    private UUID tenantId;

    @Column(name = "initiator_user_id", nullable = false)
    private UUID initiatorUserId;

    /**
     * Process state machine states: CREATED, PENDING, WAITING_APPROVAL,
     * IN_PROGRESS, APPROVED, REJECTED, CANCELLED, TIMED_OUT, FAILED, COMPLETED, COMPENSATED
     */
    @Builder.Default
    @Column(name = "current_state", nullable = false, length = 50)
    private String currentState = "CREATED";

    @Builder.Default
    @Column(name = "priority", nullable = false, length = 20)
    private String priority = "NORMAL";

    @Column(name = "due_at")
    private OffsetDateTime dueAt;

    @Column(name = "completed_at")
    private OffsetDateTime completedAt;

    @Column(name = "correlation_key", length = 150)
    private String correlationKey;

    @Column(name = "business_context", columnDefinition = "jsonb")
    private String businessContext;

    @Builder.Default
    @Column(name = "retry_count", nullable = false)
    private Integer retryCount = 0;
}
