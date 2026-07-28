package com.hyperlofy.backend.security.entity;

import com.hyperlofy.backend.common.entity.BaseEntity;
import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.Table;
import lombok.*;
import org.hibernate.annotations.SQLDelete;
import org.hibernate.annotations.SQLRestriction;

import java.time.OffsetDateTime;
import java.util.UUID;

@Entity
@Table(name = "privacy_consents")
@SQLDelete(sql = "UPDATE privacy_consents SET deleted = true WHERE id = ?")
@SQLRestriction("deleted = false")
@Getter
@Setter
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class PrivacyConsent extends BaseEntity {

    @Column(name = "user_id", nullable = false)
    private UUID userId;

    @Column(name = "consent_type", nullable = false, length = 100)
    private String consentType; // MARKETING, ANALYTICS, THIRD_PARTY_SHARING

    @Builder.Default
    @Column(name = "granted", nullable = false)
    private Boolean granted = true;

    @Column(name = "ip_address", length = 50)
    private String ipAddress;

    @Builder.Default
    @Column(name = "granted_at")
    private OffsetDateTime grantedAt = OffsetDateTime.now();
}
