package com.hyperlofy.backend.governance.entity;

import com.hyperlofy.backend.common.entity.BaseEntity;
import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.Table;
import lombok.*;
import org.hibernate.annotations.SQLDelete;
import org.hibernate.annotations.SQLRestriction;

import java.time.OffsetDateTime;

@Entity
@Table(name = "production_certifications")
@SQLDelete(sql = "UPDATE production_certifications SET deleted = true WHERE id = ?")
@SQLRestriction("deleted = false")
@Getter
@Setter
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class ProductionCertification extends BaseEntity {

    @Column(name = "certification_code", nullable = false, unique = true, length = 100)
    private String certificationCode;

    @Builder.Default
    @Column(name = "platform_version", nullable = false, length = 50)
    private String platformVersion = "1.0.0-SNAPSHOT";

    @Builder.Default
    @Column(name = "certified_by", nullable = false, length = 150)
    private String certifiedBy = "Chief Enterprise Architect";

    @Builder.Default
    @Column(name = "architecture_certified", nullable = false)
    private Boolean architectureCertified = true;

    @Builder.Default
    @Column(name = "security_certified", nullable = false)
    private Boolean securityCertified = true;

    @Builder.Default
    @Column(name = "performance_certified", nullable = false)
    private Boolean performanceCertified = true;

    @Builder.Default
    @Column(name = "reliability_certified", nullable = false)
    private Boolean reliabilityCertified = true;

    @Builder.Default
    @Column(name = "compliance_certified", nullable = false)
    private Boolean complianceCertified = true;

    @Builder.Default
    @Column(name = "data_certified", nullable = false)
    private Boolean dataCertified = true;

    @Builder.Default
    @Column(name = "api_certified", nullable = false)
    private Boolean apiCertified = true;

    @Builder.Default
    @Column(name = "infrastructure_certified", nullable = false)
    private Boolean infrastructureCertified = true;

    @Builder.Default
    @Column(name = "operations_certified", nullable = false)
    private Boolean operationsCertified = true;

    @Builder.Default
    @Column(name = "overall_status", nullable = false, length = 30)
    private String overallStatus = "PRODUCTION_READY";

    @Column(name = "certification_notes", columnDefinition = "TEXT")
    private String certificationNotes;

    @Builder.Default
    @Column(name = "certified_at", nullable = false)
    private OffsetDateTime certifiedAt = OffsetDateTime.now();
}
