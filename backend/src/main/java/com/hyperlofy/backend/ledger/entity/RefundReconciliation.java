package com.hyperlofy.backend.ledger.entity;

import com.hyperlofy.backend.common.entity.BaseEntity;
import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.Table;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;
import org.hibernate.annotations.SQLDelete;
import org.hibernate.annotations.SQLRestriction;

import java.math.BigDecimal;
import java.util.UUID;

@Entity
@Table(name = "refund_reconciliations")
@SQLDelete(sql = "UPDATE refund_reconciliations SET deleted = true WHERE id = ?")
@SQLRestriction("deleted = false")
@Getter
@Setter
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class RefundReconciliation extends BaseEntity {

    @Column(name = "order_id", nullable = false)
    private UUID orderId;

    @Column(name = "refund_type", nullable = false, length = 30)
    private String refundType;

    @Column(name = "escrow_status_at_refund", nullable = false, length = 30)
    private String escrowStatusAtRefund;

    @Column(name = "total_order_amount", nullable = false, precision = 12, scale = 2)
    private BigDecimal totalOrderAmount;

    @Column(name = "refund_amount", nullable = false, precision = 12, scale = 2)
    private BigDecimal refundAmount;

    @Builder.Default
    @Column(name = "merchant_adjustment", nullable = false, precision = 12, scale = 2)
    private BigDecimal merchantAdjustment = BigDecimal.ZERO;

    @Builder.Default
    @Column(name = "agent_adjustment", nullable = false, precision = 12, scale = 2)
    private BigDecimal agentAdjustment = BigDecimal.ZERO;

    @Builder.Default
    @Column(name = "platform_adjustment", nullable = false, precision = 12, scale = 2)
    private BigDecimal platformAdjustment = BigDecimal.ZERO;

    @Builder.Default
    @Column(name = "status", nullable = false, length = 30)
    private String status = "COMPLETED";

    @Column(name = "reason", length = 255)
    private String reason;
}
