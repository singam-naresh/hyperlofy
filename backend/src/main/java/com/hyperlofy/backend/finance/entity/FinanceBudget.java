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
@Table(name = "finance_budgets")
@SQLDelete(sql = "UPDATE finance_budgets SET deleted = true WHERE id = ?")
@SQLRestriction("deleted = false")
@Getter
@Setter
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class FinanceBudget extends BaseEntity {

    @Column(name = "budget_code", nullable = false, unique = true, length = 50)
    private String budgetCode;

    @Column(name = "period_code", nullable = false, length = 30)
    private String periodCode;

    @Column(name = "allocated_amount", nullable = false, precision = 16, scale = 2)
    private BigDecimal allocatedAmount;

    @Builder.Default
    @Column(name = "spent_amount", nullable = false, precision = 16, scale = 2)
    private BigDecimal spentAmount = BigDecimal.ZERO;

    @Builder.Default
    @Column(name = "status", nullable = false, length = 30)
    private String status = "APPROVED";
}
