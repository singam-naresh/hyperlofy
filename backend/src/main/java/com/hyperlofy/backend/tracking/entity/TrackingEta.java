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
@Table(name = "tracking_eta")
@SQLDelete(sql = "UPDATE tracking_eta SET deleted = true WHERE id = ?")
@SQLRestriction("deleted = false")
@Getter
@Setter
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class TrackingEta extends BaseEntity {

    @Column(name = "tracking_session_id", nullable = false)
    private UUID trackingSessionId;

    @Column(name = "remaining_distance_km", nullable = false)
    private Double remainingDistanceKm;

    @Column(name = "remaining_duration_minutes", nullable = false)
    private Integer remainingDurationMinutes;

    @Column(name = "calculated_eta", nullable = false)
    private ZonedDateTime calculatedEta;

    @Builder.Default
    @Column(name = "average_speed_kmh")
    private Double averageSpeedKmh = 25.0;
}
