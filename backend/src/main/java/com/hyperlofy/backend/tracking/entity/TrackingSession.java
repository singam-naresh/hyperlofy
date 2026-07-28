package com.hyperlofy.backend.tracking.entity;

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
@Table(name = "tracking_sessions")
@SQLDelete(sql = "UPDATE tracking_sessions SET deleted = true WHERE id = ?")
@SQLRestriction("deleted = false")
@Getter
@Setter
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class TrackingSession extends BaseEntity {

    @Column(name = "order_id", nullable = false, unique = true)
    private UUID orderId;

    @Column(name = "driver_id", nullable = false)
    private UUID driverId;

    @Builder.Default
    @Column(name = "status", nullable = false, length = 40)
    private String status = "TRACKING_INITIALIZED";

    @Builder.Default
    @Column(name = "started_at")
    private ZonedDateTime startedAt = ZonedDateTime.now();

    @Column(name = "completed_at")
    private ZonedDateTime completedAt;
}
