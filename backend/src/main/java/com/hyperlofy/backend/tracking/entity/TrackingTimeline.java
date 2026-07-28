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
@Table(name = "tracking_timeline")
@SQLDelete(sql = "UPDATE tracking_timeline SET deleted = true WHERE id = ?")
@SQLRestriction("deleted = false")
@Getter
@Setter
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class TrackingTimeline extends BaseEntity {

    @Column(name = "tracking_session_id", nullable = false)
    private UUID trackingSessionId;

    @Column(name = "event_name", nullable = false, length = 50)
    private String eventName;

    @Column(name = "description")
    private String description;

    @Builder.Default
    @Column(name = "event_time")
    private ZonedDateTime eventTime = ZonedDateTime.now();
}
