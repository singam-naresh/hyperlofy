package com.hyperlofy.backend.settlement.entity;

import com.hyperlofy.backend.common.entity.BaseEntity;
import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.Table;
import lombok.*;
import org.hibernate.annotations.SQLDelete;
import org.hibernate.annotations.SQLRestriction;

import java.math.BigDecimal;
import java.time.ZonedDateTime;
import java.util.UUID;

@Entity
@Table(name = "settlement_payouts")
@SQLDelete(sql = "UPDATE settlement_payouts SET deleted = true WHERE id = ?")
@SQLRestriction("deleted = false")
@Getter
@Setter
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class SettlementPayout extends BaseEntity {

    @Column(name = "settlement_id", nullable = false)
    private UUID settlementId;

    @Column(name = "payout_reference", nullable = false, unique = true, length = 100)
    private String payoutReference;

    @Column(name = "bank_account_number", nullable = false, length = 50)
    private String bankAccountNumber;

    @Column(name = "ifsc_code", nullable = false, length = 20)
    private String ifscCode;

    @Column(name = "amount", nullable = false, precision = 14, scale = 2)
    private BigDecimal amount;

    @Builder.Default
    @Column(name = "status", nullable = false, length = 30)
    private String status = "PROCESSING"; // PROCESSING, SETTLED, FAILED

    @Column(name = "processed_at")
    private ZonedDateTime processedAt;
}
