package com.hyperlofy.backend.multitenancy.entity;

import com.hyperlofy.backend.common.entity.BaseEntity;
import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.Table;
import lombok.*;
import org.hibernate.annotations.SQLDelete;
import org.hibernate.annotations.SQLRestriction;

import java.util.UUID;

@Entity
@Table(name = "tenant_identity_providers")
@SQLDelete(sql = "UPDATE tenant_identity_providers SET deleted = true WHERE id = ?")
@SQLRestriction("deleted = false")
@Getter
@Setter
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class TenantIdentityProvider extends BaseEntity {

    @Column(name = "tenant_id", nullable = false)
    private UUID tenantId;

    @Column(name = "provider_name", nullable = false, length = 100)
    private String providerName;

    @Builder.Default
    @Column(name = "provider_type", nullable = false, length = 30)
    private String providerType = "OIDC"; // SAML2, OIDC, OKTA, AZURE_AD

    @Column(name = "issuer_url", nullable = false, length = 255)
    private String issuerUrl;

    @Column(name = "client_id", nullable = false, length = 150)
    private String clientId;

    @Builder.Default
    @Column(name = "status", nullable = false, length = 30)
    private String status = "ACTIVE";
}
