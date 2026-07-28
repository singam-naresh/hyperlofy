package com.hyperlofy.backend.marketplace.entity;

import com.hyperlofy.backend.common.entity.BaseEntity;
import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.Table;
import lombok.*;
import org.hibernate.annotations.SQLDelete;
import org.hibernate.annotations.SQLRestriction;

import java.util.UUID;

@Entity
@Table(name = "inventory_transactions")
@SQLDelete(sql = "UPDATE inventory_transactions SET deleted = true WHERE id = ?")
@SQLRestriction("deleted = false")
@Getter
@Setter
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class InventoryTransaction extends BaseEntity {

    @Column(name = "variant_id", nullable = false)
    private UUID variantId;

    @Column(name = "transaction_type", nullable = false, length = 50)
    private String transactionType; // RESERVE, RELEASE, SALE, RETURN, DAMAGE, MANUAL_ADJUSTMENT

    @Column(name = "quantity_changed", nullable = false)
    private Integer quantityChanged;

    @Column(name = "stock_before", nullable = false)
    private Integer stockBefore;

    @Column(name = "stock_after", nullable = false)
    private Integer stockAfter;

    @Column(name = "reason", length = 255)
    private String reason;

    @Column(name = "performed_by")
    private UUID performedBy;
}
