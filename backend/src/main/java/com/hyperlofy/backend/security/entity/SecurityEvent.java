package com.hyperlofy.backend.security.entity;

import com.hyperlofy.backend.common.entity.BaseEntity;
import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.Table;
import lombok.*;
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

    @Column(name = "email", length = 100)
    private String email;

    @Column(name = "event_type", nullable = false, length = 50)
    private String eventType; // LOGIN_SUCCESS, LOGIN_FAILURE, PASSWORD_CHANGED, ACCOUNT_LOCKED

    @Column(name = "ip_address", length = 45)
    private String ipAddress;

    @Column(name = "user_agent", length = 255)
    private String userAgent;

    @Column(name = "device_fingerprint", length = 255)
    private String deviceFingerprint;

    @Builder.Default
    @Column(name = "severity", length = 20)
    private String severity = "INFO";

    @Column(name = "description", columnDefinition = "TEXT")
    private String description;

    @Builder.Default
    @Column(name = "risk_score")
    private Double riskScore = 0.0;

    @Column(name = "details", columnDefinition = "TEXT")
    private String details;
}
