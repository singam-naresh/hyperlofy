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
@Table(name = "tracking_locations")
@SQLDelete(sql = "UPDATE tracking_locations SET deleted = true WHERE id = ?")
@SQLRestriction("deleted = false")
@Getter
@Setter
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class TrackingLocation extends BaseEntity {

    @Column(name = "tracking_session_id", nullable = false)
    private UUID trackingSessionId;

    @Column(name = "latitude", nullable = false)
    private Double latitude;

    @Column(name = "longitude", nullable = false)
    private Double longitude;

    @Builder.Default
    @Column(name = "heading")
    private Double heading = 0.0;

    @Builder.Default
    @Column(name = "speed_kmh")
    private Double speedKmh = 0.0;

    @Builder.Default
    @Column(name = "accuracy_meters")
    private Double accuracyMeters = 5.0;

    @Builder.Default
    @Column(name = "device_timestamp")
    private ZonedDateTime deviceTimestamp = ZonedDateTime.now();
}
