package com.hyperlofy.backend.merchant.entity;

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
@Table(name = "merchant_ledgers")
@SQLDelete(sql = "UPDATE merchant_ledgers SET deleted = true WHERE id = ?")
@SQLRestriction("deleted = false")
@Getter
@Setter
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class MerchantLedger extends BaseEntity {

    @Column(name = "merchant_id", nullable = false)
    private UUID merchantId;

    @Column(name = "order_id", nullable = false)
    private UUID orderId;

    @Column(name = "item_subtotal", nullable = false, precision = 12, scale = 2)
    private BigDecimal itemSubtotal;

    @Column(name = "merchant_share", nullable = false, precision = 12, scale = 2)
    private BigDecimal merchantShare;

    @Builder.Default
    @Column(name = "status", nullable = false, length = 30)
    private String status = "UNPAID";

    @Column(name = "settlement_batch_id")
    private UUID settlementBatchId;
}
