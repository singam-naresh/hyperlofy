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
@Table(name = "payment_transactions")
@SQLDelete(sql = "UPDATE payment_transactions SET deleted = true WHERE id = ?")
@SQLRestriction("deleted = false")
@Getter
@Setter
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class PaymentTransaction extends BaseEntity {

    @Column(name = "payment_id", nullable = false)
    private UUID paymentId;

    @Column(name = "transaction_type", nullable = false, length = 30)
    private String transactionType; // AUTHORIZE, CAPTURE, REFUND, VOID

    @Column(name = "amount", nullable = false, precision = 12, scale = 2)
    private BigDecimal amount;

    @Builder.Default
    @Column(name = "status", nullable = false, length = 30)
    private String status = "SUCCESS";

    @Column(name = "provider_transaction_id", length = 100)
    private String providerTransactionId;

    @Column(name = "error_code", length = 50)
    private String errorCode;

    @Column(name = "error_message")
    private String errorMessage;
}
