package com.hyperlofy.backend.finance.entity;

import com.hyperlofy.backend.common.entity.BaseEntity;
import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.Table;
import lombok.*;
import org.hibernate.annotations.SQLDelete;
import org.hibernate.annotations.SQLRestriction;

import java.time.LocalDate;
import java.time.ZonedDateTime;

@Entity
@Table(name = "finance_accounting_periods")
@SQLDelete(sql = "UPDATE finance_accounting_periods SET deleted = true WHERE id = ?")
@SQLRestriction("deleted = false")
@Getter
@Setter
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class FinanceAccountingPeriod extends BaseEntity {

    @Column(name = "period_code", nullable = false, unique = true, length = 30)
    private String periodCode; // e.g. 2026-07

    @Column(name = "start_date", nullable = false)
    private LocalDate startDate;

    @Column(name = "end_date", nullable = false)
    private LocalDate endDate;

    @Builder.Default
    @Column(name = "is_closed")
    private Boolean isClosed = false;

    @Column(name = "closed_at")
    private ZonedDateTime closedAt;

    @Column(name = "closed_by", length = 100)
    private String closedBy;
}
