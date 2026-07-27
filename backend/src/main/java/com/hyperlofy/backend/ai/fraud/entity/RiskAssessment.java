package com.hyperlofy.backend.ai.fraud.entity;

import com.hyperlofy.backend.common.entity.BaseEntity;
import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.Table;
import lombok.*;
import org.hibernate.annotations.SQLDelete;
import org.hibernate.annotations.SQLRestriction;

import java.util.UUID;

@Entity
@Table(name = "risk_assessments")
@SQLDelete(sql = "UPDATE risk_assessments SET deleted = true WHERE id = ?")
@SQLRestriction("deleted = false")
@Getter
@Setter
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class RiskAssessment extends BaseEntity {

    @Column(name = "target_id", nullable = false)
    private UUID targetId;

    @Column(name = "target_type", nullable = false, length = 50)
    private String targetType; // ORDER, CUSTOMER, MERCHANT, DRIVER, REFUND

    @Column(name = "risk_level", nullable = false, length = 20)
    private String riskLevel; // LOW, MEDIUM, HIGH, CRITICAL

    @Builder.Default
    @Column(name = "risk_score", nullable = false)
    private Double riskScore = 0.0;

    @Column(name = "triggered_rules", columnDefinition = "TEXT")
    private String triggeredRules;
}
