package com.hyperlofy.backend.zone.dto;

import jakarta.validation.constraints.DecimalMax;
import jakarta.validation.constraints.DecimalMin;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Size;
import lombok.Builder;
import lombok.Data;

@Data
@Builder
public class ZoneRequest {

    @NotBlank(message = "Zone name is required")
    @Size(min = 2, max = 100, message = "Zone name must be between 2 and 100 characters")
    private String name;

    @NotNull(message = "Center latitude is required")
    @DecimalMin(value = "-90.0", message = "Latitude must be >= -90")
    @DecimalMax(value = "90.0", message = "Latitude must be <= 90")
    private Double centerLatitude;

    @NotNull(message = "Center longitude is required")
    @DecimalMin(value = "-180.0", message = "Longitude must be >= -180")
    @DecimalMax(value = "180.0", message = "Longitude must be <= 180")
    private Double centerLongitude;

    @NotNull(message = "Radius is required")
    @DecimalMin(value = "0.1", message = "Radius must be >= 0.1 km")
    @DecimalMax(value = "100.0", message = "Radius must be <= 100 km")
    private Double radiusKm;
}
