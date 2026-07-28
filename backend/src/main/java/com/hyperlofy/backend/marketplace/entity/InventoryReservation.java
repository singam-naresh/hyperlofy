package com.hyperlofy.backend.marketplace.entity;

import com.hyperlofy.backend.common.entity.BaseEntity;
import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.Table;
import lombok.*;
import org.hibernate.annotations.SQLDelete;
import org.hibernate.annotations.SQLRestriction;

import java.time.ZonedDateTime;
import java.util.UUID;

@Entity
@Table(name = "inventory_reservations")
@SQLDelete(sql = "UPDATE inventory_reservations SET deleted = true WHERE id = ?")
@SQLRestriction("deleted = false")
@Getter
@Setter
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class InventoryReservation extends BaseEntity {

    @Column(name = "customer_id", nullable = false)
    private UUID customerId;

    @Column(name = "variant_id", nullable = false)
    private UUID variantId;

    @Column(name = "reserved_quantity", nullable = false)
    private Integer reservedQuantity;

    @Builder.Default
    @Column(name = "reservation_status", nullable = false, length = 30)
    private String reservationStatus = "RESERVED"; // RESERVED, COMMITTED, EXPIRED, RELEASED

    @Column(name = "expires_at", nullable = false)
    private ZonedDateTime expiresAt;
}
