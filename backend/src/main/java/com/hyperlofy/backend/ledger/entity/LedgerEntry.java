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
@Table(name = "ledger_entries")
@SQLDelete(sql = "UPDATE ledger_entries SET deleted = true WHERE id = ?")
@SQLRestriction("deleted = false")
@Getter
@Setter
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class LedgerEntry extends BaseEntity {

    @Column(name = "debit_account", nullable = false, length = 100)
    private String debitAccount;

    @Column(name = "credit_account", nullable = false, length = 100)
    private String creditAccount;

    @Column(name = "amount", nullable = false, precision = 15, scale = 2)
    private BigDecimal amount;

    @Column(name = "reference_id")
    private UUID referenceId;

    @Column(name = "order_id")
    private UUID orderId;

    @Column(name = "payment_id")
    private UUID paymentId;

    @Column(name = "transaction_type", nullable = false, length = 50)
    private String transactionType; // DEPOSIT, ES_HELD, ES_RELEASED, COMMISSION_CHARGED, PAYOUT, REFUNDED

    @Column(name = "description", length = 255)
    private String description;
}
