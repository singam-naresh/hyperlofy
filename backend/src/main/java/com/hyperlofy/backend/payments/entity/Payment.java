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
@Table(name = "payments")
@SQLDelete(sql = "UPDATE payments SET deleted = true WHERE id = ?")
@SQLRestriction("deleted = false")
@Getter
@Setter
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class Payment extends BaseEntity {

    @Column(name = "order_id", nullable = false)
    private UUID orderId;

    @Column(name = "customer_id", nullable = false)
    private UUID customerId;

    @Column(name = "payment_method", nullable = false, length = 30)
    private String paymentMethod; // UPI, CREDIT_CARD, DEBIT_CARD, WALLET, COD

    @Builder.Default
    @Column(name = "provider_name", nullable = false, length = 40)
    private String providerName = "RAZORPAY";

    @Column(name = "provider_payment_id", length = 100)
    private String providerPaymentId;

    @Column(name = "amount", nullable = false, precision = 12, scale = 2)
    private BigDecimal amount;

    @Builder.Default
    @Column(name = "currency", nullable = false, length = 10)
    private String currency = "INR";

    @Builder.Default
    @Column(name = "status", nullable = false, length = 30)
    private String status = "PAYMENT_CREATED";
}
