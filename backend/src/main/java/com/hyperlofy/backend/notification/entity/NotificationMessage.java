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
@Table(name = "notification_messages")
@SQLDelete(sql = "UPDATE notification_messages SET deleted = true WHERE id = ?")
@SQLRestriction("deleted = false")
@Getter
@Setter
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class NotificationMessage extends BaseEntity {

    @Column(name = "recipient_id", nullable = false)
    private UUID recipientId;

    @Column(name = "channel", nullable = false, length = 30)
    private String channel; // PUSH, SMS, EMAIL, WHATSAPP, IN_APP

    @Column(name = "template_code", length = 100)
    private String templateCode;

    @Column(name = "title")
    private String title;

    @Column(name = "body", nullable = false)
    private String body;

    @Builder.Default
    @Column(name = "status", nullable = false, length = 30)
    private String status = "QUEUED"; // QUEUED, SENT, DELIVERED, READ, FAILED

    @Column(name = "provider_name", length = 50)
    private String providerName;

    @Builder.Default
    @Column(name = "delivery_attempts", nullable = false)
    private Integer deliveryAttempts = 0;

    @Column(name = "delivered_at")
    private ZonedDateTime deliveredAt;

    @Column(name = "read_at")
    private ZonedDateTime readAt;
}
