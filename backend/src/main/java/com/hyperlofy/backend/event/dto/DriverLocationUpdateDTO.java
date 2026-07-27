package com.hyperlofy.backend.event.dto;

import io.swagger.v3.oas.annotations.media.Schema;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

import java.util.UUID;

@Getter
@Setter
@Builder
@NoArgsConstructor
@AllArgsConstructor
@Schema(description = "Real-Time Delivery Driver GPS Coordinates Update DTO")
public class DriverLocationUpdateDTO {

    @Schema(description = "Order ID")
    private UUID orderId;

    @Schema(description = "Delivery Driver Agent ID")
    private UUID agentId;

    @Schema(description = "Current GPS Latitude")
    private Double latitude;

    @Schema(description = "Current GPS Longitude")
    private Double longitude;

    @Schema(description = "Estimated Arrival Time Minutes", example = "12")
    private Integer estimatedArrivalMinutes;
}
