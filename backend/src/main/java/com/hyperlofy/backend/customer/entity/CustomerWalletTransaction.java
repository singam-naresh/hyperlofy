package com.hyperlofy.backend.customer.entity;

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
@Table(name = "customer_wallet_transactions")
@SQLDelete(sql = "UPDATE customer_wallet_transactions SET deleted = true WHERE id = ?")
@SQLRestriction("deleted = false")
@Getter
@Setter
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class CustomerWalletTransaction extends BaseEntity {

    @Column(name = "wallet_id", nullable = false)
    private UUID walletId;

    @Column(name = "amount", nullable = false, precision = 12, scale = 2)
    private BigDecimal amount;

    @Column(name = "transaction_type", nullable = false, length = 30)
    private String transactionType; // CREDIT, DEBIT, REFUND, CASHBACK

    @Column(name = "description", nullable = false, length = 255)
    private String description;

    @Column(name = "reference_id")
    private UUID referenceId;
}
