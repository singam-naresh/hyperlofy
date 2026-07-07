package com.hyperlofy.backend.zone.dto;

import lombok.Builder;
import lombok.Data;

import java.math.BigDecimal;
import java.util.UUID;

@Data
@Builder
public class DeliveryFeeCalculationResponse {
    private Double distanceKm;
    private BigDecimal deliveryFee;
    private UUID zoneId;
    private String zoneName;
    private Boolean withinZoneBounds;
}
