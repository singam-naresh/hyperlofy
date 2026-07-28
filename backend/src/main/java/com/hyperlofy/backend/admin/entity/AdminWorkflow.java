package com.hyperlofy.backend.admin.entity;

import com.hyperlofy.backend.common.entity.BaseEntity;
import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.Table;
import lombok.*;
import org.hibernate.annotations.SQLDelete;
import org.hibernate.annotations.SQLRestriction;

@Entity
@Table(name = "admin_workflows")
@SQLDelete(sql = "UPDATE admin_workflows SET deleted = true WHERE id = ?")
@SQLRestriction("deleted = false")
@Getter
@Setter
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class AdminWorkflow extends BaseEntity {

    @Column(name = "workflow_name", nullable = false, unique = true, length = 100)
    private String workflowName;

    @Column(name = "trigger_event", nullable = false, length = 100)
    private String triggerEvent;

    @Column(name = "current_step", nullable = false, length = 50)
    private String currentStep;

    @Builder.Default
    @Column(name = "status", nullable = false, length = 30)
    private String status = "ACTIVE"; // ACTIVE, PAUSED, COMPLETED

    @Builder.Default
    @Column(name = "sla_hours", nullable = false)
    private Integer slaHours = 24;
}
