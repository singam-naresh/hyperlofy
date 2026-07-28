package com.hyperlofy.backend.finance.entity;

import com.hyperlofy.backend.common.entity.BaseEntity;
import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.Table;
import lombok.*;
import org.hibernate.annotations.SQLDelete;
import org.hibernate.annotations.SQLRestriction;

import java.math.BigDecimal;

@Entity
@Table(name = "finance_financial_controls")
@SQLDelete(sql = "UPDATE finance_financial_controls SET deleted = true WHERE id = ?")
@SQLRestriction("deleted = false")
@Getter
@Setter
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class FinanceFinancialControl extends BaseEntity {

    @Column(name = "control_code", nullable = false, unique = true, length = 50)
    private String controlCode;

    @Column(name = "control_name", nullable = false, length = 100)
    private String controlName;

    @Column(name = "threshold_amount", nullable = false, precision = 14, scale = 2)
    private BigDecimal thresholdAmount;

    @Builder.Default
    @Column(name = "requires_dual_approval")
    private Boolean requiresDualApproval = true;
}
