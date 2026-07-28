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
@Table(name = "settlements")
@SQLDelete(sql = "UPDATE settlements SET deleted = true WHERE id = ?")
@SQLRestriction("deleted = false")
@Getter
@Setter
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class Settlement extends BaseEntity {

    @Column(name = "order_id", nullable = false)
    private UUID orderId;

    @Column(name = "payee_id", nullable = false)
    private UUID payeeId;

    @Column(name = "payee_type", nullable = false, length = 30)
    private String payeeType; // MERCHANT, DRIVER, PLATFORM, TAX

    @Column(name = "gross_amount", nullable = false, precision = 14, scale = 2)
    private BigDecimal grossAmount;

    @Builder.Default
    @Column(name = "platform_commission", nullable = false, precision = 14, scale = 2)
    private BigDecimal platformCommission = BigDecimal.ZERO;

    @Builder.Default
    @Column(name = "tax_amount", nullable = false, precision = 14, scale = 2)
    private BigDecimal taxAmount = BigDecimal.ZERO;

    @Column(name = "net_amount", nullable = false, precision = 14, scale = 2)
    private BigDecimal netAmount;

    @Builder.Default
    @Column(name = "status", nullable = false, length = 30)
    private String status = "SETTLEMENT_CREATED";

    @Column(name = "scheduled_payout_at")
    private ZonedDateTime scheduledPayoutAt;
}
