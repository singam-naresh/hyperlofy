package com.hyperlofy.backend.engagement.entity;

import com.hyperlofy.backend.common.entity.BaseEntity;
import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.Table;
import lombok.*;
import org.hibernate.annotations.SQLDelete;
import org.hibernate.annotations.SQLRestriction;

import java.math.BigDecimal;
import java.util.UUID;

@Entity
@Table(name = "customer_segments")
@SQLDelete(sql = "UPDATE customer_segments SET deleted = true WHERE id = ?")
@SQLRestriction("deleted = false")
@Getter
@Setter
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class CustomerSegment extends BaseEntity {

    @Column(name = "customer_id", nullable = false)
    private UUID customerId;

    @Column(name = "segment_name", nullable = false, length = 80)
    private String segmentName; // NEW, ACTIVE, LOYAL, VIP, HIGH_VALUE, PRICE_SENSITIVE, CHURN_RISK

    @Builder.Default
    @Column(name = "confidence_score", nullable = false, precision = 5, scale = 2)
    private BigDecimal confidenceScore = new BigDecimal("98.00");

    @Builder.Default
    @Column(name = "assigned_by_model", nullable = false, length = 100)
    private String assignedByModel = "gemini-customer-segmentation-v2";

    @Column(name = "tenant_id")
    private UUID tenantId;
}
