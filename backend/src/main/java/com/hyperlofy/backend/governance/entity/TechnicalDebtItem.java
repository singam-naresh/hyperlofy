package com.hyperlofy.backend.governance.entity;

import com.hyperlofy.backend.common.entity.BaseEntity;
import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.Table;
import lombok.*;
import org.hibernate.annotations.SQLDelete;
import org.hibernate.annotations.SQLRestriction;

import java.math.BigDecimal;
import java.time.LocalDate;
import java.util.UUID;

@Entity
@Table(name = "technical_debt_items")
@SQLDelete(sql = "UPDATE technical_debt_items SET deleted = true WHERE id = ?")
@SQLRestriction("deleted = false")
@Getter
@Setter
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class TechnicalDebtItem extends BaseEntity {

    @Column(name = "item_code", nullable = false, unique = true, length = 100)
    private String itemCode;

    @Column(name = "title", nullable = false, length = 255)
    private String title;

    @Column(name = "description", nullable = false, columnDefinition = "TEXT")
    private String description;

    @Builder.Default
    @Column(name = "severity", nullable = false, length = 30)
    private String severity = "MEDIUM"; // LOW, MEDIUM, HIGH, CRITICAL

    @Builder.Default
    @Column(name = "risk_score", nullable = false, precision = 5, scale = 2)
    private BigDecimal riskScore = new BigDecimal("25.00");

    @Builder.Default
    @Column(name = "status", nullable = false, length = 30)
    private String status = "OPEN"; // OPEN, IN_PROGRESS, RESOLVED, WAIVED

    @Column(name = "owner_user_id", nullable = false)
    private UUID ownerUserId;

    @Column(name = "target_resolution_date")
    private LocalDate targetResolutionDate;
}
