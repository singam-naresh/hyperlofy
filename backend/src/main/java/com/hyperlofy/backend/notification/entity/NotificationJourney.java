package com.hyperlofy.backend.notification.entity;

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
@Table(name = "notification_journeys")
@SQLDelete(sql = "UPDATE notification_journeys SET deleted = true WHERE id = ?")
@SQLRestriction("deleted = false")
@Getter
@Setter
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class NotificationJourney extends BaseEntity {

    @Column(name = "journey_name", nullable = false, length = 100)
    private String journeyName;

    @Column(name = "user_id", nullable = false)
    private UUID userId;

    @Column(name = "current_step", nullable = false, length = 50)
    private String currentStep;

    @Builder.Default
    @Column(name = "status", nullable = false, length = 30)
    private String status = "IN_PROGRESS"; // IN_PROGRESS, COMPLETED, CANCELLED

    @Builder.Default
    @Column(name = "started_at")
    private ZonedDateTime startedAt = ZonedDateTime.now();
}
