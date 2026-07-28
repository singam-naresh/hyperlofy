package com.hyperlofy.backend.settlement.entity;

import com.hyperlofy.backend.common.entity.BaseEntity;
import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.Table;
import lombok.*;
import org.hibernate.annotations.SQLDelete;
import org.hibernate.annotations.SQLRestriction;

import java.math.BigDecimal;
import java.util.UUID;

@Entity
@Table(name = "settlement_risk_events")
@SQLDelete(sql = "UPDATE settlement_risk_events SET deleted = true WHERE id = ?")
@SQLRestriction("deleted = false")
@Getter
@Setter
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class SettlementRiskEvent extends BaseEntity {

    @Column(name = "settlement_id", nullable = false)
    private UUID settlementId;

    @Column(name = "risk_type", nullable = false, length = 50)
    private String riskType; // DUPLICATE_PAYOUT, ABNORMAL_VELOCITY, HIGH_VALUE_TRANSFER

    @Column(name = "risk_score", nullable = false, precision = 5, scale = 2)
    private BigDecimal riskScore;

    @Builder.Default
    @Column(name = "action_taken", nullable = false, length = 30)
    private String actionTaken = "FLAGGED_FOR_REVIEW";
}
