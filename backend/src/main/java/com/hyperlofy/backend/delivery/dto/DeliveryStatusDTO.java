package com.hyperlofy.backend.delivery.dto;

import io.swagger.v3.oas.annotations.media.Schema;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.util.UUID;

@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
@Schema(description = "Delivery Partner Work Availability Status DTO")
public class DeliveryStatusDTO {

    @Schema(description = "Agent User ID")
    private UUID agentId;

    @Schema(description = "Work Status (ONLINE, OFFLINE, BUSY, ON_BREAK, AVAILABLE, UNAVAILABLE)")
    private String workStatus;

    @Schema(description = "Availability Flag")
    private boolean available;

    @Schema(description = "Current GPS Latitude")
    private Double currentGpsLatitude;

    @Schema(description = "Current GPS Longitude")
    private Double currentGpsLongitude;
}
