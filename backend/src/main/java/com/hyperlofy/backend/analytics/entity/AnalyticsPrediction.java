package com.hyperlofy.backend.analytics.entity;

import com.hyperlofy.backend.common.entity.BaseEntity;
import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.Table;
import lombok.*;
import org.hibernate.annotations.SQLDelete;
import org.hibernate.annotations.SQLRestriction;

import java.math.BigDecimal;

@Entity
@Table(name = "analytics_predictions")
@SQLDelete(sql = "UPDATE analytics_predictions SET deleted = true WHERE id = ?")
@SQLRestriction("deleted = false")
@Getter
@Setter
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class AnalyticsPrediction extends BaseEntity {

    @Column(name = "prediction_target", nullable = false, length = 100)
    private String predictionTarget; // DEMAND, REVENUE, DRIVER_DEMAND, DELIVERY_TIME

    @Builder.Default
    @Column(name = "model_version", nullable = false, length = 50)
    private String modelVersion = "v1.0.0";

    @Builder.Default
    @Column(name = "predicted_value", nullable = false, precision = 16, scale = 4)
    private BigDecimal predictedValue = BigDecimal.ZERO;

    @Builder.Default
    @Column(name = "confidence_score", nullable = false, precision = 5, scale = 4)
    private BigDecimal confidenceScore = new BigDecimal("0.9500");

    @Builder.Default
    @Column(name = "forecast_horizon", nullable = false, length = 30)
    private String forecastHorizon = "24_HOURS";
}
