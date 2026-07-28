package com.hyperlofy.backend.ai.platform.entity;

import com.hyperlofy.backend.common.entity.BaseEntity;
import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.Table;
import lombok.*;
import org.hibernate.annotations.SQLDelete;
import org.hibernate.annotations.SQLRestriction;

@Entity
@Table(name = "ai_governance")
@SQLDelete(sql = "UPDATE ai_governance SET deleted = true WHERE id = ?")
@SQLRestriction("deleted = false")
@Getter
@Setter
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class AiGovernance extends BaseEntity {

    @Column(name = "model_name", nullable = false, length = 100)
    private String modelName;

    @Builder.Default
    @Column(name = "approval_status", nullable = false, length = 30)
    private String approvalStatus = "APPROVED"; // PENDING, APPROVED, REJECTED, DEPRECATED

    @Column(name = "approved_by", nullable = false, length = 100)
    private String approvedBy;

    @Builder.Default
    @Column(name = "policy_version", nullable = false, length = 30)
    private String policyVersion = "v1.0.0";
}
