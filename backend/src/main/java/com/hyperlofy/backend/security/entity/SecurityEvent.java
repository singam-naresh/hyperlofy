package com.hyperlofy.backend.security.entity;

import com.hyperlofy.backend.common.entity.BaseEntity;
import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.Table;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;
import org.hibernate.annotations.SQLDelete;
import org.hibernate.annotations.SQLRestriction;

import java.util.UUID;

@Entity
@Table(name = "security_events")
@SQLDelete(sql = "UPDATE security_events SET deleted = true WHERE id = ?")
@SQLRestriction("deleted = false")
@Getter
@Setter
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class SecurityEvent extends BaseEntity {

    @Column(name = "user_id")
    private UUID userId;

    @Column(name = "email", nullable = false, length = 100)
    private String email;

    @Column(name = "event_type", nullable = false, length = 100)
    private String eventType; // LOGIN_ANOMALY, RATE_LIMIT_EXCEEDED, BRUTE_FORCE_ATTEMPT, BRUTE_FORCE_LOCK, REFRESH_TOKEN_ROTATION, JWT_REVOCATION

    @Column(name = "ip_address", nullable = false, length = 45)
    private String ipAddress;

    @Column(name = "device_fingerprint", length = 255)
    private String deviceFingerprint;

    @Column(name = "severity", nullable = false, length = 20)
    private String severity; // INFO, LOW, MEDIUM, HIGH, CRITICAL

    @Column(name = "description", columnDefinition = "TEXT")
    private String description;
}
