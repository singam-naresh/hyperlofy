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
@Table(name = "notification_consent")
@SQLDelete(sql = "UPDATE notification_consent SET deleted = true WHERE id = ?")
@SQLRestriction("deleted = false")
@Getter
@Setter
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class NotificationConsent extends BaseEntity {

    @Column(name = "user_id", nullable = false, unique = true)
    private UUID userId;

    @Builder.Default
    @Column(name = "marketing_consent")
    private Boolean marketingConsent = true;

    @Builder.Default
    @Column(name = "transactional_consent")
    private Boolean transactionalConsent = true;

    @Builder.Default
    @Column(name = "consent_given_at")
    private ZonedDateTime consentGivenAt = ZonedDateTime.now();
}
