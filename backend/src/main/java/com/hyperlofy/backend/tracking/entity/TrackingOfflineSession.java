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
@Table(name = "tracking_offline_sessions")
@SQLDelete(sql = "UPDATE tracking_offline_sessions SET deleted = true WHERE id = ?")
@SQLRestriction("deleted = false")
@Getter
@Setter
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class TrackingOfflineSession extends BaseEntity {

    @Column(name = "tracking_session_id", nullable = false)
    private UUID trackingSessionId;

    @Builder.Default
    @Column(name = "buffered_points_count", nullable = false)
    private Integer bufferedPointsCount = 0;

    @Builder.Default
    @Column(name = "sync_status", nullable = false, length = 30)
    private String syncStatus = "PENDING"; // PENDING, COMPLETED, CONFLICT_RESOLVED

    @Column(name = "synced_at")
    private ZonedDateTime syncedAt;
}
