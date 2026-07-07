package com.hyperlofy.backend.zone.dto;

import lombok.Builder;
import lombok.Data;

import java.math.BigDecimal;
import java.util.UUID;

@Data
@Builder
public class PricingSlabResponse {
    private UUID id;
    private UUID zoneId;
    private Double minDistanceKm;
    private Double maxDistanceKm;
    private BigDecimal basePrice;
    private BigDecimal perKmPrice;
}
