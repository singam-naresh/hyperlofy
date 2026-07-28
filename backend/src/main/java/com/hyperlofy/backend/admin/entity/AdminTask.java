package com.hyperlofy.backend.admin.entity;

import com.hyperlofy.backend.common.entity.BaseEntity;
import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.Table;
import lombok.*;
import org.hibernate.annotations.SQLDelete;
import org.hibernate.annotations.SQLRestriction;

import java.time.ZonedDateTime;
import java.util.UUID;

@Entity
@Table(name = "admin_tasks")
@SQLDelete(sql = "UPDATE admin_tasks SET deleted = true WHERE id = ?")
@SQLRestriction("deleted = false")
@Getter
@Setter
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class AdminTask extends BaseEntity {

    @Column(name = "task_number", nullable = false, unique = true, length = 100)
    private String taskNumber;

    @Column(name = "workflow_id")
    private UUID workflowId;

    @Column(name = "title", nullable = false, length = 255)
    private String title;

    @Column(name = "assigned_agent", length = 100)
    private String assignedAgent;

    @Builder.Default
    @Column(name = "priority", nullable = false, length = 30)
    private String priority = "MEDIUM";

    @Builder.Default
    @Column(name = "status", nullable = false, length = 30)
    private String status = "PENDING"; // PENDING, IN_PROGRESS, COMPLETED, ESCALATED

    @Column(name = "due_at")
    private ZonedDateTime dueAt;
}
