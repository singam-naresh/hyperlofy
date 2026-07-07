package com.hyperlofy.backend.agent.entity;

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
@Table(name = "withdrawal_requests")
@SQLDelete(sql = "UPDATE withdrawal_requests SET deleted = true WHERE id = ?")
@SQLRestriction("deleted = false")
@Getter
@Setter
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class WithdrawalRequest extends BaseEntity {

    @Column(name = "agent_id", nullable = false)
    private UUID agentId;

    @Column(name = "amount", nullable = false, precision = 12, scale = 2)
    private BigDecimal amount;

    @Column(name = "status", nullable = false, length = 30) // PENDING, APPROVED, REJECTED, TRANSFERRED
    private String status = "PENDING";

    @Column(name = "rejection_reason")
    private String rejectionReason;

    @Column(name = "payout_transaction_reference")
    private String payoutTransactionReference;
}
