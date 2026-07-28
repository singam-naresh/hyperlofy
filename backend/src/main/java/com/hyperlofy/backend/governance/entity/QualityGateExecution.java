package com.hyperlofy.backend.governance.entity;

import com.hyperlofy.backend.common.entity.BaseEntity;
import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.Table;
import lombok.*;
import org.hibernate.annotations.SQLDelete;
import org.hibernate.annotations.SQLRestriction;

@Entity
@Table(name = "quality_gate_executions")
@SQLDelete(sql = "UPDATE quality_gate_executions SET deleted = true WHERE id = ?")
@SQLRestriction("deleted = false")
@Getter
@Setter
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class QualityGateExecution extends BaseEntity {

    @Column(name = "execution_code", nullable = false, unique = true, length = 100)
    private String executionCode;

    @Column(name = "gate_name", nullable = false, length = 100)
    private String gateName; // BUILD_GATE, TEST_GATE, SECURITY_SCAN, DEPENDENCY_SCAN, ARCHITECTURE_SCAN

    @Builder.Default
    @Column(name = "status", nullable = false, length = 30)
    private String status = "PASSED"; // PASSED, FAILED, WARNING

    @Builder.Default
    @Column(name = "total_checks", nullable = false)
    private Integer totalChecks = 10;

    @Builder.Default
    @Column(name = "passed_checks", nullable = false)
    private Integer passedChecks = 10;

    @Builder.Default
    @Column(name = "failed_checks", nullable = false)
    private Integer failedChecks = 0;

    @Column(name = "execution_summary", columnDefinition = "TEXT")
    private String executionSummary;
}
