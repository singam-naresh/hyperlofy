package com.hyperlofy.backend.sre.entity;

import com.hyperlofy.backend.common.entity.BaseEntity;
import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.Table;
import lombok.*;
import org.hibernate.annotations.SQLDelete;
import org.hibernate.annotations.SQLRestriction;

import java.math.BigDecimal;

@Entity
@Table(name = "platform_runbooks")
@SQLDelete(sql = "UPDATE platform_runbooks SET deleted = true WHERE id = ?")
@SQLRestriction("deleted = false")
@Getter
@Setter
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class PlatformRunbook extends BaseEntity {

    @Column(name = "runbook_name", nullable = false, unique = true, length = 150)
    private String runbookName;

    @Column(name = "trigger_condition", nullable = false, length = 150)
    private String triggerCondition;

    @Builder.Default
    @Column(name = "execution_mode", nullable = false, length = 30)
    private String executionMode = "AUTOMATED"; // AUTOMATED, MANUAL

    @Builder.Default
    @Column(name = "success_rate_pct", nullable = false, precision = 5, scale = 2)
    private BigDecimal successRatePct = new BigDecimal("99.50");
}
