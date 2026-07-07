package com.hyperlofy.backend.ledger.entity;

import com.hyperlofy.backend.common.entity.BaseEntity;
import jakarta.persistence.*;
import lombok.*;
import org.hibernate.annotations.SQLDelete;
import org.hibernate.annotations.SQLRestriction;

import java.math.BigDecimal;
import java.time.OffsetDateTime;
import java.util.UUID;

@Entity
@Table(name = "escrow_transactions")
@SQLDelete(sql = "UPDATE escrow_transactions SET deleted = true WHERE id = ?")
@SQLRestriction("deleted = false")
@Getter
@Setter
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class EscrowTransaction extends BaseEntity {

    @Column(name = "order_id", nullable = false)
    private UUID orderId;

    @Column(name = "payment_id", nullable = false)
    private UUID paymentId;

    @Column(name = "amount", nullable = false, precision = 15, scale = 2)
    private BigDecimal amount;

    @Column(name = "status", nullable = false, length = 30)
    private String status; // HELD, RELEASED, REFUNDED

    @Column(name = "released_at")
    private OffsetDateTime releasedAt;
}
