package com.hyperlofy.backend.observability.entity;

import com.hyperlofy.backend.common.entity.BaseEntity;
import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.Table;
import lombok.*;
import org.hibernate.annotations.SQLDelete;
import org.hibernate.annotations.SQLRestriction;

import java.math.BigDecimal;

@Entity
@Table(name = "telemetry_events")
@SQLDelete(sql = "UPDATE telemetry_events SET deleted = true WHERE id = ?")
@SQLRestriction("deleted = false")
@Getter
@Setter
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class TelemetryEvent extends BaseEntity {

    @Column(name = "service_name", nullable = false, length = 100)
    private String serviceName;

    @Column(name = "event_type", nullable = false, length = 50)
    private String eventType; // METRIC, LOG, SPAN, SYNTHETIC

    @Column(name = "metric_name", nullable = false, length = 150)
    private String metricName;

    @Column(name = "metric_value", nullable = false, precision = 16, scale = 4)
    private BigDecimal metricValue;

    @Column(name = "correlation_id", nullable = false, length = 100)
    private String correlationId;
}
