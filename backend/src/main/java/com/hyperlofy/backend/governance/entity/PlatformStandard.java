package com.hyperlofy.backend.governance.entity;

import com.hyperlofy.backend.common.entity.BaseEntity;
import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.Table;
import lombok.*;
import org.hibernate.annotations.SQLDelete;
import org.hibernate.annotations.SQLRestriction;

import java.math.BigDecimal;

@Entity
@Table(name = "platform_standards")
@SQLDelete(sql = "UPDATE platform_standards SET deleted = true WHERE id = ?")
@SQLRestriction("deleted = false")
@Getter
@Setter
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class PlatformStandard extends BaseEntity {

    @Column(name = "standard_key", nullable = false, unique = true, length = 100)
    private String standardKey;

    @Column(name = "standard_name", nullable = false, length = 150)
    private String standardName;

    @Column(name = "category", nullable = false, length = 80)
    private String category; // ARCHITECTURE, API, DATABASE, SECURITY, CODING

    @Column(name = "description", nullable = false, columnDefinition = "TEXT")
    private String description;

    @Builder.Default
    @Column(name = "compliance_score", nullable = false, precision = 5, scale = 2)
    private BigDecimal complianceScore = new BigDecimal("100.00");

    @Builder.Default
    @Column(name = "is_mandatory", nullable = false)
    private Boolean isMandatory = true;
}
