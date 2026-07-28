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
@Table(name = "notification_engagement")
@SQLDelete(sql = "UPDATE notification_engagement SET deleted = true WHERE id = ?")
@SQLRestriction("deleted = false")
@Getter
@Setter
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class NotificationEngagement extends BaseEntity {

    @Column(name = "message_id", nullable = false)
    private UUID messageId;

    @Column(name = "event_type", nullable = false, length = 30)
    private String eventType; // OPENED, CLICKED, CONVERTED, BOVNCED

    @Builder.Default
    @Column(name = "timestamp")
    private ZonedDateTime timestamp = ZonedDateTime.now();
}
