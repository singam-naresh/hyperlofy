package com.hyperlofy.backend.settlement.entity;

import com.hyperlofy.backend.common.entity.BaseEntity;
import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.Table;
import lombok.*;
import org.hibernate.annotations.SQLDelete;
import org.hibernate.annotations.SQLRestriction;

import java.math.BigDecimal;

@Entity
@Table(name = "settlement_bank_routes")
@SQLDelete(sql = "UPDATE settlement_bank_routes SET deleted = true WHERE id = ?")
@SQLRestriction("deleted = false")
@Getter
@Setter
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class SettlementBankRoute extends BaseEntity {

    @Column(name = "gateway_name", nullable = false, unique = true, length = 50)
    private String gatewayName; // RAZORPAY_X, CASHFREE, ICICI_API

    @Builder.Default
    @Column(name = "priority_order", nullable = false)
    private Integer priorityOrder = 1;

    @Builder.Default
    @Column(name = "is_active")
    private Boolean isActive = true;

    @Builder.Default
    @Column(name = "success_rate", nullable = false, precision = 5, scale = 2)
    private BigDecimal successRate = new BigDecimal("99.50");

    @Builder.Default
    @Column(name = "avg_latency_ms", nullable = false)
    private Integer avgLatencyMs = 120;
}
