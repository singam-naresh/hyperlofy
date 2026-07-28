package com.hyperlofy.backend.admin.entity;

import com.hyperlofy.backend.common.entity.BaseEntity;
import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.Table;
import lombok.*;
import org.hibernate.annotations.SQLDelete;
import org.hibernate.annotations.SQLRestriction;

@Entity
@Table(name = "admin_agent_workloads")
@SQLDelete(sql = "UPDATE admin_agent_workloads SET deleted = true WHERE id = ?")
@SQLRestriction("deleted = false")
@Getter
@Setter
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class AdminAgentWorkload extends BaseEntity {

    @Column(name = "agent_user", nullable = false, unique = true, length = 100)
    private String agentUser;

    @Builder.Default
    @Column(name = "active_cases_count", nullable = false)
    private Integer activeCasesCount = 0;

    @Builder.Default
    @Column(name = "skill_category", nullable = false, length = 50)
    private String skillCategory = "GENERAL_SUPPORT";

    @Builder.Default
    @Column(name = "shift_status", nullable = false, length = 30)
    private String shiftStatus = "ON_DUTY"; // ON_DUTY, OFF_DUTY, ON_BREAK
}
