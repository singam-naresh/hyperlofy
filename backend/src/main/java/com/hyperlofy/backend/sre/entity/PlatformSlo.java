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
@Table(name = "platform_slos")
@SQLDelete(sql = "UPDATE platform_slos SET deleted = true WHERE id = ?")
@SQLRestriction("deleted = false")
@Getter
@Setter
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class PlatformSlo extends BaseEntity {

    @Column(name = "slo_name", nullable = false, unique = true, length = 150)
    private String sloName;

    @Builder.Default
    @Column(name = "target_percentage", nullable = false, precision = 5, scale = 3)
    private BigDecimal targetPercentage = new BigDecimal("99.900");

    @Builder.Default
    @Column(name = "current_percentage", nullable = false, precision = 5, scale = 3)
    private BigDecimal currentPercentage = new BigDecimal("99.950");

    @Builder.Default
    @Column(name = "error_budget_remaining_pct", nullable = false, precision = 5, scale = 2)
    private BigDecimal errorBudgetRemainingPct = new BigDecimal("85.00");
}
