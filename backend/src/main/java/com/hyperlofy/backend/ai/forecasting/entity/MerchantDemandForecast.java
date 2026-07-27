package com.hyperlofy.backend.ai.forecasting.entity;

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
@Table(name = "merchant_demand_forecasts")
@SQLDelete(sql = "UPDATE merchant_demand_forecasts SET deleted = true WHERE id = ?")
@SQLRestriction("deleted = false")
@Getter
@Setter
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class MerchantDemandForecast extends BaseEntity {

    @Column(name = "merchant_id", nullable = false)
    private UUID merchantId;

    @Column(name = "forecast_type", nullable = false, length = 50)
    private String forecastType; // HOURLY, DAILY, WEEKLY, MONTHLY

    @Builder.Default
    @Column(name = "projected_order_volume", nullable = false)
    private Integer projectedOrderVolume = 0;

    @Builder.Default
    @Column(name = "projected_revenue", nullable = false, precision = 12, scale = 2)
    private BigDecimal projectedRevenue = BigDecimal.ZERO;

    @Builder.Default
    @Column(name = "confidence_score")
    private Double confidenceScore = 0.85;

    @Column(name = "forecast_date", nullable = false, length = 50)
    private String forecastDate;
}
