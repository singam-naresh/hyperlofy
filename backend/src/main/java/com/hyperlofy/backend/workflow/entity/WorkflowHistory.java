package com.hyperlofy.backend.workflow.entity;

import com.hyperlofy.backend.common.entity.BaseEntity;
import jakarta.persistence.*;
import lombok.*;
import org.hibernate.annotations.SQLDelete;
import org.hibernate.annotations.SQLRestriction;

import java.util.UUID;

@Entity
@Table(name = "workflow_history")
@SQLDelete(sql = "UPDATE workflow_history SET deleted = true WHERE id = ?")
@SQLRestriction("deleted = false")
@Getter
@Setter
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class WorkflowHistory extends BaseEntity {

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "instance_id", nullable = false)
    private WorkflowInstance instance;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "task_id")
    private WorkflowTask task;

    /**
     * Actions: STARTED, STATE_CHANGED, TASK_CLAIMED, TASK_COMPLETED,
     * APPROVED, REJECTED, ESCALATED, COMPENSATED, TIMED_OUT, CANCELLED
     */
    @Column(name = "action", nullable = false, length = 80)
    private String action;

    @Column(name = "from_state", length = 50)
    private String fromState;

    @Column(name = "to_state", length = 50)
    private String toState;

    @Column(name = "actor_user_id")
    private UUID actorUserId;

    @Column(name = "comment", columnDefinition = "TEXT")
    private String comment;
}
