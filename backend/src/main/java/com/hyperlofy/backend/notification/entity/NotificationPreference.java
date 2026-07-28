package com.hyperlofy.backend.notification.entity;

import com.hyperlofy.backend.common.entity.BaseEntity;
import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.Table;
import lombok.*;
import org.hibernate.annotations.SQLDelete;
import org.hibernate.annotations.SQLRestriction;

import java.util.UUID;

@Entity
@Table(name = "notification_preferences")
@SQLDelete(sql = "UPDATE notification_preferences SET deleted = true WHERE id = ?")
@SQLRestriction("deleted = false")
@Getter
@Setter
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class NotificationPreference extends BaseEntity {

    @Column(name = "user_id", nullable = false, unique = true)
    private UUID userId;

    @Builder.Default
    @Column(name = "push_enabled")
    private Boolean pushEnabled = true;

    @Builder.Default
    @Column(name = "sms_enabled")
    private Boolean smsEnabled = true;

    @Builder.Default
    @Column(name = "email_enabled")
    private Boolean emailEnabled = true;

    @Builder.Default
    @Column(name = "whatsapp_enabled")
    private Boolean whatsappEnabled = true;

    @Builder.Default
    @Column(name = "quiet_hours_enabled")
    private Boolean quietHoursEnabled = false;
}
