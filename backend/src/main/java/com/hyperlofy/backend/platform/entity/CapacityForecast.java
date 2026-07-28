package com.hyperlofy.backend.platform.entity;

import com.hyperlofy.backend.common.entity.BaseEntity;
import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.Table;
import lombok.*;
import org.hibernate.annotations.SQLDelete;
import org.hibernate.annotations.SQLRestriction;

@Entity
@Table(name = "capacity_forecasts")
@SQLDelete(sql = "UPDATE capacity_forecasts SET deleted = true WHERE id = ?")
@SQLRestriction("deleted = false")
@Getter
@Setter
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class CapacityForecast extends BaseEntity {

    @Column(name = "resource_type", nullable = false, length = 50)
    private String resourceType; // DATABASE_STORAGE, REDIS_MEMORY, S3_OBJECT_STORAGE, LOG_VOLUME

    @Column(name = "forecast_days", nullable = false)
    private Integer forecastDays; // 30, 90, 180, 365

    @Column(name = "current_capacity_gb", nullable = false)
    private Double currentCapacityGb;

    @Column(name = "projected_capacity_gb", nullable = false)
    private Double projectedCapacityGb;

    @Column(name = "growth_rate_percentage", nullable = false)
    private Double growthRatePercentage;
}
