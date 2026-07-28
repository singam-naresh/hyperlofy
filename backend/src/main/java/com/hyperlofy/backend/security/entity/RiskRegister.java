package com.hyperlofy.backend.security.entity;

import com.hyperlofy.backend.common.entity.BaseEntity;
import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.Table;
import lombok.*;
import org.hibernate.annotations.SQLDelete;
import org.hibernate.annotations.SQLRestriction;

import java.math.BigDecimal;

@Entity
@Table(name = "risk_register")
@SQLDelete(sql = "UPDATE risk_register SET deleted = true WHERE id = ?")
@SQLRestriction("deleted = false")
@Getter
@Setter
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class RiskRegister extends BaseEntity {

    @Column(name = "risk_code", nullable = false, unique = true, length = 100)
    private String riskCode;

    @Column(name = "risk_title", nullable = false, length = 150)
    private String riskTitle;

    @Column(name = "category", nullable = false, length = 50)
    private String category; // CYBERSECURITY, COMPLIANCE, OPERATIONAL

    @Builder.Default
    @Column(name = "severity", nullable = false, length = 30)
    private String severity = "HIGH"; // CRITICAL, HIGH, MEDIUM, LOW

    @Builder.Default
    @Column(name = "impact_score", nullable = false, precision = 5, scale = 2)
    private BigDecimal impactScore = new BigDecimal("85.00");

    @Builder.Default
    @Column(name = "status", nullable = false, length = 30)
    private String status = "IDENTIFIED"; // IDENTIFIED, MITIGATED, ACCEPTED
}
