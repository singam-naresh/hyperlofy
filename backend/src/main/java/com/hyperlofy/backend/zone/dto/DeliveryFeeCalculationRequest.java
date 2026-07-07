package com.hyperlofy.backend.zone.dto;

import jakarta.validation.constraints.DecimalMax;
import jakarta.validation.constraints.DecimalMin;
import jakarta.validation.constraints.NotNull;
import lombok.Builder;
import lombok.Data;

import java.util.UUID;

@Data
@Builder
public class DeliveryFeeCalculationRequest {

    @NotNull(message = "Zone ID is required")
    private UUID zoneId;

    @NotNull(message = "Store latitude is required")
    @DecimalMin(value = "-90.0")
    @DecimalMax(value = "90.0")
    private Double storeLatitude;

    @NotNull(message = "Store longitude is required")
    @DecimalMin(value = "-180.0")
    @DecimalMax(value = "180.0")
    private Double storeLongitude;

    @NotNull(message = "Delivery latitude is required")
    @DecimalMin(value = "-90.0")
    @DecimalMax(value = "90.0")
    private Double deliveryLatitude;

    @NotNull(message = "Delivery longitude is required")
    @DecimalMin(value = "-180.0")
    @DecimalMax(value = "180.0")
    private Double deliveryLongitude;
}
