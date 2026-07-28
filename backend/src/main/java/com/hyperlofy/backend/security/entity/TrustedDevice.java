package com.hyperlofy.backend.security.entity;

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
@Table(name = "trusted_devices")
@SQLDelete(sql = "UPDATE trusted_devices SET deleted = true WHERE id = ?")
@SQLRestriction("deleted = false")
@Getter
@Setter
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class TrustedDevice extends BaseEntity {

    @Column(name = "user_id", nullable = false)
    private UUID userId;

    @Column(name = "device_name", nullable = false, length = 100)
    private String deviceName;

    @Column(name = "device_fingerprint", nullable = false, length = 255)
    private String deviceFingerprint;

    @Column(name = "operating_system", length = 50)
    private String operatingSystem;

    @Column(name = "browser_name", length = 50)
    private String browserName;

    @Column(name = "app_version", length = 20)
    private String appVersion;

    @Column(name = "push_notification_token", length = 255)
    private String pushNotificationToken;

    @Builder.Default
    @Column(name = "device_risk_score")
    private Double deviceRiskScore = 0.0;

    @Builder.Default
    @Column(name = "is_trusted")
    private Boolean isTrusted = true;

    @Builder.Default
    @Column(name = "last_active_at")
    private ZonedDateTime lastActiveAt = ZonedDateTime.now();
}
