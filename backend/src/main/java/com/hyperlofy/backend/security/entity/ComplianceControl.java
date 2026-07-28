package com.hyperlofy.backend.security.entity;

import com.hyperlofy.backend.common.entity.BaseEntity;
import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.Table;
import lombok.*;
import org.hibernate.annotations.SQLDelete;
import org.hibernate.annotations.SQLRestriction;

import java.time.OffsetDateTime;

@Entity
@Table(name = "compliance_controls")
@SQLDelete(sql = "UPDATE compliance_controls SET deleted = true WHERE id = ?")
@SQLRestriction("deleted = false")
@Getter
@Setter
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class ComplianceControl extends BaseEntity {

    @Column(name = "control_code", nullable = false, unique = true, length = 100)
    private String controlCode;

    @Column(name = "framework", nullable = false, length = 50)
    private String framework; // SOC2, ISO27001, GDPR, PCI_DSS

    @Column(name = "control_name", nullable = false, length = 150)
    private String controlName;

    @Builder.Default
    @Column(name = "status", nullable = false, length = 30)
    private String status = "PASSED"; // PASSED, FAILED, IN_REVIEW

    @Column(name = "evidence_url", length = 255)
    private String evidenceUrl;

    @Builder.Default
    @Column(name = "last_tested_at")
    private OffsetDateTime lastTestedAt = OffsetDateTime.now();
}
