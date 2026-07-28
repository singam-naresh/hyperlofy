package com.hyperlofy.backend.support.entity;

import com.hyperlofy.backend.common.entity.BaseEntity;
import jakarta.persistence.*;
import lombok.*;
import org.hibernate.annotations.SQLDelete;
import org.hibernate.annotations.SQLRestriction;

import java.math.BigDecimal;
import java.util.UUID;

@Entity
@Table(name = "refund_cases")
@SQLDelete(sql = "UPDATE refund_cases SET deleted = true WHERE id = ?")
@SQLRestriction("deleted = false")
@Getter
@Setter
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class RefundCase extends BaseEntity {

    @Column(name = "refund_code", nullable = false, unique = true, length = 100)
    private String refundCode;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "ticket_id", nullable = false)
    private SupportTicket ticket;

    @Column(name = "order_id", nullable = false)
    private UUID orderId;

    @Column(name = "amount", nullable = false, precision = 16, scale = 2)
    private BigDecimal amount;

    @Builder.Default
    @Column(name = "refund_reason", nullable = false, length = 100)
    private String refundReason = "WRONG_PRODUCT";

    @Builder.Default
    @Column(name = "refund_method", nullable = false, length = 50)
    private String refundMethod = "WALLET"; // WALLET, ORIGINAL_PAYMENT, INSTANT_BANK

    @Builder.Default
    @Column(name = "status", nullable = false, length = 30)
    private String status = "REQUESTED"; // REQUESTED, APPROVED, REJECTED, COMPLETED

    @Column(name = "tenant_id")
    private UUID tenantId;
}
