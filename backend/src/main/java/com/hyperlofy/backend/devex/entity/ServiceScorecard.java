package com.hyperlofy.backend.devex.entity;

import com.hyperlofy.backend.common.entity.BaseEntity;
import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.Table;
import lombok.*;
import org.hibernate.annotations.SQLDelete;
import org.hibernate.annotations.SQLRestriction;

import java.math.BigDecimal;

@Entity
@Table(name = "service_scorecards")
@SQLDelete(sql = "UPDATE service_scorecards SET deleted = true WHERE id = ?")
@SQLRestriction("deleted = false")
@Getter
@Setter
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class ServiceScorecard extends BaseEntity {

    @Column(name = "service_name", nullable = false, unique = true, length = 100)
    private String serviceName;

    @Builder.Default
    @Column(name = "overall_score", nullable = false, precision = 5, scale = 2)
    private BigDecimal overallScore = new BigDecimal("98.50");

    @Builder.Default
    @Column(name = "grade", nullable = false, length = 5)
    private String grade = "A+";

    @Builder.Default
    @Column(name = "security_score", nullable = false, precision = 5, scale = 2)
    private BigDecimal securityScore = new BigDecimal("100.00");

    @Builder.Default
    @Column(name = "observability_score", nullable = false, precision = 5, scale = 2)
    private BigDecimal observabilityScore = new BigDecimal("96.00");

    @Builder.Default
    @Column(name = "documentation_score", nullable = false, precision = 5, scale = 2)
    private BigDecimal documentationScore = new BigDecimal("99.50");
}
