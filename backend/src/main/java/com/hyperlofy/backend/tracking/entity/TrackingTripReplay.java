package com.hyperlofy.backend.tracking.entity;

import com.hyperlofy.backend.common.entity.BaseEntity;
import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.Table;
import lombok.*;
import org.hibernate.annotations.SQLDelete;
import org.hibernate.annotations.SQLRestriction;

import java.util.UUID;

@Entity
@Table(name = "tracking_trip_replays")
@SQLDelete(sql = "UPDATE tracking_trip_replays SET deleted = true WHERE id = ?")
@SQLRestriction("deleted = false")
@Getter
@Setter
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class TrackingTripReplay extends BaseEntity {

    @Column(name = "tracking_session_id", nullable = false)
    private UUID trackingSessionId;

    @Column(name = "replay_data_json", nullable = false, columnDefinition = "TEXT")
    private String replayDataJson;

    @Builder.Default
    @Column(name = "total_stops_detected")
    private Integer totalStopsDetected = 0;

    @Builder.Default
    @Column(name = "idle_duration_minutes")
    private Integer idleDurationMinutes = 0;

    @Builder.Default
    @Column(name = "average_speed_kmh")
    private Double averageSpeedKmh = 25.0;
}
