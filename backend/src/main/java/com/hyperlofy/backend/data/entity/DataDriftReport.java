package com.hyperlofy.backend.data.entity;

import com.hyperlofy.backend.common.entity.BaseEntity;
import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.Table;
import lombok.*;
import org.hibernate.annotations.SQLDelete;
import org.hibernate.annotations.SQLRestriction;

import java.math.BigDecimal;
import java.time.OffsetDateTime;

@Entity
@Table(name = "data_drift_reports")
@SQLDelete(sql = "UPDATE data_drift_reports SET deleted = true WHERE id = ?")
@SQLRestriction("deleted = false")
@Getter
@Setter
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class DataDriftReport extends BaseEntity {

    @Column(name = "model_id", nullable = false, length = 100)
    private String modelId;

    @Column(name = "feature_name", nullable = false, length = 100)
    private String featureName;

    @Builder.Default
    @Column(name = "drift_score", nullable = false, precision = 5, scale = 4)
    private BigDecimal driftScore = new BigDecimal("0.0125");

    @Builder.Default
    @Column(name = "drift_detected", nullable = false)
    private Boolean driftDetected = false;

    @Builder.Default
    @Column(name = "report_timestamp")
    private OffsetDateTime reportTimestamp = OffsetDateTime.now();
}
