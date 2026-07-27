package com.hyperlofy.backend.platform.entity;

import com.hyperlofy.backend.common.entity.BaseEntity;
import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.Table;
import lombok.*;
import org.hibernate.annotations.SQLDelete;
import org.hibernate.annotations.SQLRestriction;

import java.time.OffsetDateTime;

@Entity
@Table(name = "system_notifications")
@SQLDelete(sql = "UPDATE system_notifications SET deleted = true WHERE id = ?")
@SQLRestriction("deleted = false")
@Getter
@Setter
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class SystemNotification extends BaseEntity {

    @Column(name = "title", nullable = false, length = 200)
    private String title;

    @Column(name = "message", nullable = false, columnDefinition = "TEXT")
    private String message;

    @Column(name = "target_group", nullable = false, length = 50)
    private String targetGroup; // ALL, CUSTOMERS, MERCHANTS, AGENTS

    @Column(name = "channel", nullable = false, length = 30)
    private String channel; // PUSH, EMAIL, SMS, ALL

    @Column(name = "scheduled_at")
    private OffsetDateTime scheduledAt;

    @Column(name = "sent_at")
    private OffsetDateTime sentAt;

    @Builder.Default
    @Column(name = "status", length = 30)
    private String status = "SCHEDULED";

    @Builder.Default
    @Column(name = "recipient_count")
    private Integer recipientCount = 0;
}
