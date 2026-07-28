package com.hyperlofy.backend.matching.entity;

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
@Table(name = "matching_reservations")
@SQLDelete(sql = "UPDATE matching_reservations SET deleted = true WHERE id = ?")
@SQLRestriction("deleted = false")
@Getter
@Setter
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class MatchingReservation extends BaseEntity {

    @Column(name = "order_id", nullable = false)
    private UUID orderId;

    @Column(name = "reserved_driver_id", nullable = false)
    private UUID reservedDriverId;

    @Column(name = "scheduled_time", nullable = false)
    private ZonedDateTime scheduledTime;

    @Builder.Default
    @Column(name = "status", nullable = false, length = 30)
    private String status = "RESERVED"; // RESERVED, CONFIRMED, EXPIRED, CANCELLED

    @Column(name = "expires_at", nullable = false)
    private ZonedDateTime expiresAt;
}
