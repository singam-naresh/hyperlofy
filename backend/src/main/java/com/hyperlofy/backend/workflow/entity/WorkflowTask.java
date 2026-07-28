package com.hyperlofy.backend.workflow.entity;

import com.hyperlofy.backend.common.entity.BaseEntity;
import jakarta.persistence.*;
import lombok.*;
import org.hibernate.annotations.SQLDelete;
import org.hibernate.annotations.SQLRestriction;

import java.time.OffsetDateTime;
import java.util.UUID;

@Entity
@Table(name = "workflow_tasks")
@SQLDelete(sql = "UPDATE workflow_tasks SET deleted = true WHERE id = ?")
@SQLRestriction("deleted = false")
@Getter
@Setter
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class WorkflowTask extends BaseEntity {

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "instance_id", nullable = false)
    private WorkflowInstance instance;

    @Column(name = "task_name", nullable = false, length = 150)
    private String taskName;

    /**
     * Task types: HUMAN_APPROVAL, SERVICE_CALL, KAFKA_PUBLISH, TIMER, SAGA_STEP
     */
    @Builder.Default
    @Column(name = "task_type", nullable = false, length = 50)
    private String taskType = "HUMAN_APPROVAL";

    /**
     * Task status: PENDING, CLAIMED, IN_PROGRESS, COMPLETED, DELEGATED, ESCALATED, TIMED_OUT, FAILED
     */
    @Builder.Default
    @Column(name = "status", nullable = false, length = 50)
    private String status = "PENDING";

    @Column(name = "assignee_user_id")
    private UUID assigneeUserId;

    @Column(name = "candidate_group", length = 100)
    private String candidateGroup;

    @Builder.Default
    @Column(name = "priority", nullable = false, length = 20)
    private String priority = "NORMAL";

    @Column(name = "due_at")
    private OffsetDateTime dueAt;

    @Column(name = "claimed_at")
    private OffsetDateTime claimedAt;

    @Column(name = "completed_at")
    private OffsetDateTime completedAt;

    @Column(name = "completion_reason", columnDefinition = "TEXT")
    private String completionReason;

    @Builder.Default
    @Column(name = "is_compensation", nullable = false)
    private Boolean isCompensation = false;
}
