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
import java.time.OffsetDateTime;

@Entity
@Table(name = "settlement_batches")
@SQLDelete(sql = "UPDATE settlement_batches SET deleted = true WHERE id = ?")
@SQLRestriction("deleted = false")
@Getter
@Setter
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class SettlementBatch extends BaseEntity {

    @Column(name = "batch_status", nullable = false, length = 30)
    private String batchStatus; // OPEN, PROCESSING, COMPLETED, FAILED

    @Column(name = "total_settlement_amount", nullable = false, precision = 15, scale = 2)
    private BigDecimal totalSettlementAmount = BigDecimal.ZERO;

    @Column(name = "processed_at")
    private OffsetDateTime processedAt;
}
