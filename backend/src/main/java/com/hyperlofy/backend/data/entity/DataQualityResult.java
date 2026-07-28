package com.hyperlofy.backend.data.entity;

import com.hyperlofy.backend.common.entity.BaseEntity;
import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.Table;
import lombok.*;
import org.hibernate.annotations.SQLDelete;
import org.hibernate.annotations.SQLRestriction;

import java.time.OffsetDateTime;
import java.util.UUID;

@Entity
@Table(name = "data_quality_results")
@SQLDelete(sql = "UPDATE data_quality_results SET deleted = true WHERE id = ?")
@SQLRestriction("deleted = false")
@Getter
@Setter
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class DataQualityResult extends BaseEntity {

    @Column(name = "dataset_id", nullable = false)
    private UUID datasetId;

    @Column(name = "rule_name", nullable = false, length = 100)
    private String ruleName;

    @Column(name = "rule_type", nullable = false, length = 50)
    private String ruleType; // COMPLETENESS, ACCURACY, FRESHNESS, ANOMALY

    @Builder.Default
    @Column(name = "status", nullable = false, length = 30)
    private String status = "PASSED"; // PASSED, FAILED, QUARANTINED

    @Builder.Default
    @Column(name = "records_checked", nullable = false)
    private Integer recordsChecked = 0;

    @Builder.Default
    @Column(name = "records_failed", nullable = false)
    private Integer recordsFailed = 0;

    @Builder.Default
    @Column(name = "executed_at")
    private OffsetDateTime executedAt = OffsetDateTime.now();
}
