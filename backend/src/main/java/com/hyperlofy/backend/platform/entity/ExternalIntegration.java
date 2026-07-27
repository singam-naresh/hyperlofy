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
@Table(name = "external_integrations")
@SQLDelete(sql = "UPDATE external_integrations SET deleted = true WHERE id = ?")
@SQLRestriction("deleted = false")
@Getter
@Setter
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class ExternalIntegration extends BaseEntity {

    @Column(name = "provider_name", nullable = false, unique = true, length = 100)
    private String providerName; // RAZORPAY, GEMINI, OPENAI, MAPS, FIREBASE

    @Column(name = "api_key_masked", length = 100)
    private String apiKeyMasked;

    @Builder.Default
    @Column(name = "is_active", nullable = false)
    private Boolean isActive = true;

    @Column(name = "last_validated_at")
    private OffsetDateTime lastValidatedAt;

    @Builder.Default
    @Column(name = "status", length = 30)
    private String status = "HEALTHY";
}
