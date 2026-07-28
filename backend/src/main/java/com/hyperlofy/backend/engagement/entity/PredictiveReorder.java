package com.hyperlofy.backend.engagement.entity;

import com.hyperlofy.backend.common.entity.BaseEntity;
import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.Table;
import lombok.*;
import org.hibernate.annotations.SQLDelete;
import org.hibernate.annotations.SQLRestriction;

import java.math.BigDecimal;
import java.time.OffsetDateTime;
import java.util.UUID;

@Entity
@Table(name = "predictive_reorders")
@SQLDelete(sql = "UPDATE predictive_reorders SET deleted = true WHERE id = ?")
@SQLRestriction("deleted = false")
@Getter
@Setter
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class PredictiveReorder extends BaseEntity {

    @Column(name = "prediction_code", nullable = false, unique = true, length = 100)
    private String predictionCode;

    @Column(name = "customer_id", nullable = false)
    private UUID customerId;

    @Column(name = "product_id", nullable = false)
    private UUID productId;

    @Column(name = "predicted_reorder_date", nullable = false)
    private OffsetDateTime predictedReorderDate;

    @Builder.Default
    @Column(name = "confidence_score", nullable = false, precision = 5, scale = 2)
    private BigDecimal confidenceScore = new BigDecimal("94.50");

    @Builder.Default
    @Column(name = "reminder_schedule_cron", length = 100)
    private String reminderScheduleCron = "0 9 * * *";

    @Builder.Default
    @Column(name = "status", nullable = false, length = 30)
    private String status = "SCHEDULED"; // SCHEDULED, REMINDED, CONFIRMED, SKIPPED

    @Column(name = "tenant_id")
    private UUID tenantId;
}
