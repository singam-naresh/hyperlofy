package com.hyperlofy.backend.ai.platform.entity;

import com.hyperlofy.backend.common.entity.BaseEntity;
import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.Table;
import lombok.*;
import org.hibernate.annotations.SQLDelete;
import org.hibernate.annotations.SQLRestriction;

@Entity
@Table(name = "ai_agent_runs")
@SQLDelete(sql = "UPDATE ai_agent_runs SET deleted = true WHERE id = ?")
@SQLRestriction("deleted = false")
@Getter
@Setter
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class AiAgentRun extends BaseEntity {

    @Column(name = "agent_name", nullable = false, length = 100)
    private String agentName;

    @Column(name = "task_goal", nullable = false, length = 255)
    private String taskGoal;

    @Builder.Default
    @Column(name = "status", nullable = false, length = 30)
    private String status = "COMPLETED"; // RUNNING, COMPLETED, FAILED

    @Column(name = "execution_steps_json")
    private String executionStepsJson;
}
