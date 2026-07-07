package com.hyperlofy.backend.zone.dto;

import jakarta.validation.constraints.DecimalMin;
import jakarta.validation.constraints.NotNull;
import lombok.Builder;
import lombok.Data;

import java.math.BigDecimal;
import java.util.UUID;

@Data
@Builder
public class PricingSlabRequest {

    @NotNull(message = "Zone ID is required")
    private UUID zoneId;

    @NotNull(message = "Minimum distance is required")
    @DecimalMin(value = "0.0", message = "Min distance must be at least 0.0 km")
    private Double minDistanceKm;

    @NotNull(message = "Maximum distance is required")
    @DecimalMin(value = "0.1", message = "Max distance must be greater than 0.0 km")
    private Double maxDistanceKm;

    @NotNull(message = "Base price is required")
    @DecimalMin(value = "0.00", message = "Base price cannot be negative")
    private BigDecimal basePrice;

    @NotNull(message = "Per km price is required")
    @DecimalMin(value = "0.00", message = "Per km price cannot be negative")
    private BigDecimal perKmPrice;
}
