package com.hyperlofy.backend.payments.entity;

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
@Table(name = "payment_refunds")
@SQLDelete(sql = "UPDATE payment_refunds SET deleted = true WHERE id = ?")
@SQLRestriction("deleted = false")
@Getter
@Setter
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class PaymentRefund extends BaseEntity {

    @Column(name = "payment_id", nullable = false)
    private UUID paymentId;

    @Column(name = "refund_amount", nullable = false, precision = 12, scale = 2)
    private BigDecimal refundAmount;

    @Column(name = "reason")
    private String reason;

    @Builder.Default
    @Column(name = "status", nullable = false, length = 30)
    private String status = "REFUND_PENDING";

    @Column(name = "provider_refund_id", length = 100)
    private String providerRefundId;
}
