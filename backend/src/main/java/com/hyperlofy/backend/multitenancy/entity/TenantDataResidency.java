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
@Table(name = "tenant_data_residency")
@SQLDelete(sql = "UPDATE tenant_data_residency SET deleted = true WHERE id = ?")
@SQLRestriction("deleted = false")
@Getter
@Setter
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class TenantDataResidency extends BaseEntity {

    @Column(name = "tenant_id", nullable = false, unique = true)
    private UUID tenantId;

    @Builder.Default
    @Column(name = "data_region", nullable = false, length = 50)
    private String dataRegion = "ap-south-1"; // ap-south-1, us-east-1, eu-central-1

    @Builder.Default
    @Column(name = "compliance_standard", nullable = false, length = 100)
    private String complianceStandard = "GDPR_SOC2";

    @Column(name = "encryption_key_arn", length = 255)
    private String encryptionKeyArn;
}
