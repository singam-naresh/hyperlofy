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

import java.time.OffsetDateTime;
import java.util.UUID;

@Entity
@Table(name = "device_sessions")
@SQLDelete(sql = "UPDATE device_sessions SET deleted = true WHERE id = ?")
@SQLRestriction("deleted = false")
@Getter
@Setter
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class DeviceSession extends BaseEntity {

    @Column(name = "user_id", nullable = false)
    private UUID userId;

    @Column(name = "refresh_token", nullable = false, unique = true, length = 255)
    private String refreshToken;

    @Column(name = "device_fingerprint", nullable = false, length = 255)
    private String deviceFingerprint;

    @Column(name = "ip_address", nullable = false, length = 45)
    private String ipAddress;

    @Column(name = "revoked", nullable = false)
    private boolean revoked = false;

    @Column(name = "expires_at", nullable = false)
    private OffsetDateTime expiresAt;
}
