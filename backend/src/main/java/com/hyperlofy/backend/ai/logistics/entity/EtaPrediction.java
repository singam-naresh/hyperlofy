package com.hyperlofy.backend.ai.logistics.entity;

import com.hyperlofy.backend.common.entity.BaseEntity;
import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.Table;
import lombok.*;
import org.hibernate.annotations.SQLDelete;
import org.hibernate.annotations.SQLRestriction;

import java.util.UUID;

@Entity
@Table(name = "eta_predictions")
@SQLDelete(sql = "UPDATE eta_predictions SET deleted = true WHERE id = ?")
@SQLRestriction("deleted = false")
@Getter
@Setter
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class EtaPrediction extends BaseEntity {

    @Column(name = "order_id", nullable = false)
    private UUID orderId;

    @Builder.Default
    @Column(name = "estimated_prep_minutes", nullable = false)
    private Integer estimatedPrepMinutes = 15;

    @Builder.Default
    @Column(name = "estimated_travel_minutes", nullable = false)
    private Integer estimatedTravelMinutes = 20;

    @Builder.Default
    @Column(name = "total_eta_minutes", nullable = false)
    private Integer totalEtaMinutes = 35;

    @Builder.Default
    @Column(name = "confidence_score")
    private Double confidenceScore = 0.90;

    @Builder.Default
    @Column(name = "prediction_strategy", nullable = false, length = 50)
    private String predictionStrategy = "HYBRID_WEIGHTED_AVERAGE";
}
