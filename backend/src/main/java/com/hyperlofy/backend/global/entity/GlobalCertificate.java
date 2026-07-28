package com.hyperlofy.backend.global.entity;

import com.hyperlofy.backend.common.entity.BaseEntity;
import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.Table;
import lombok.*;
import org.hibernate.annotations.SQLDelete;
import org.hibernate.annotations.SQLRestriction;

import java.time.OffsetDateTime;

@Entity
@Table(name = "global_certificates")
@SQLDelete(sql = "UPDATE global_certificates SET deleted = true WHERE id = ?")
@SQLRestriction("deleted = false")
@Getter
@Setter
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class GlobalCertificate extends BaseEntity {

    @Column(name = "domain_name", nullable = false, unique = true, length = 255)
    private String domainName;

    @Builder.Default
    @Column(name = "certificate_authority", nullable = false, length = 100)
    private String certificateAuthority = "LetsEncrypt";

    @Builder.Default
    @Column(name = "status", nullable = false, length = 30)
    private String status = "VALID"; // VALID, EXPIRING_SOON, RENEWED, REVOKED

    @Builder.Default
    @Column(name = "expires_at", nullable = false)
    private OffsetDateTime expiresAt = OffsetDateTime.now().plusDays(90);

    @Builder.Default
    @Column(name = "auto_renew", nullable = false)
    private Boolean autoRenew = true;

    @Builder.Default
    @Column(name = "dns_provider", nullable = false, length = 50)
    private String dnsProvider = "Route53";
}
