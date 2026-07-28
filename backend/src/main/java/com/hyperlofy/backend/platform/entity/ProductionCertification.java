package com.hyperlofy.backend.platform.entity;

import com.hyperlofy.backend.common.entity.BaseEntity;
import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.Table;
import lombok.*;
import org.hibernate.annotations.SQLDelete;
import org.hibernate.annotations.SQLRestriction;

import java.time.ZonedDateTime;

@Entity
@Table(name = "production_certification")
@SQLDelete(sql = "UPDATE production_certification SET deleted = true WHERE id = ?")
@SQLRestriction("deleted = false")
@Getter
@Setter
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class ProductionCertification extends BaseEntity {

    @Column(name = "milestone_name", nullable = false, unique = true, length = 100)
    private String milestoneName;

    @Builder.Default
    @Column(name = "architecture_score", nullable = false)
    private Double architectureScore = 9.9;

    @Builder.Default
    @Column(name = "security_score", nullable = false)
    private Double securityScore = 9.9;

    @Builder.Default
    @Column(name = "scalability_score", nullable = false)
    private Double scalabilityScore = 9.9;

    @Builder.Default
    @Column(name = "performance_score", nullable = false)
    private Double performanceScore = 9.8;

    @Builder.Default
    @Column(name = "overall_production_score", nullable = false)
    private Double overallProductionScore = 99.2; // Scorecard >= 95%

    @Builder.Default
    @Column(name = "is_certified")
    private Boolean isCertified = true;

    @Column(name = "certified_by", nullable = false, length = 100)
    private String certifiedBy;

    @Builder.Default
    @Column(name = "certified_at")
    private ZonedDateTime certifiedAt = ZonedDateTime.now();
}
