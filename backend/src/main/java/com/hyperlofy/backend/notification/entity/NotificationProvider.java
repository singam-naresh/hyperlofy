package com.hyperlofy.backend.notification.entity;

import com.hyperlofy.backend.common.entity.BaseEntity;
import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.Table;
import lombok.*;
import org.hibernate.annotations.SQLDelete;
import org.hibernate.annotations.SQLRestriction;

@Entity
@Table(name = "notification_providers")
@SQLDelete(sql = "UPDATE notification_providers SET deleted = true WHERE id = ?")
@SQLRestriction("deleted = false")
@Getter
@Setter
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class NotificationProvider extends BaseEntity {

    @Column(name = "provider_name", nullable = false, unique = true, length = 50)
    private String providerName; // FCM, TWILIO, MSG91, SENDGRID, WHATSAPP

    @Column(name = "channel", nullable = false, length = 30)
    private String channel;

    @Builder.Default
    @Column(name = "priority", nullable = false)
    private Integer priority = 1;

    @Builder.Default
    @Column(name = "is_active")
    private Boolean isActive = true;

    @Builder.Default
    @Column(name = "health_status", nullable = false, length = 30)
    private String healthStatus = "HEALTHY";
}
