package com.hyperlofy.backend.zone.dto;

import lombok.Builder;
import lombok.Data;

import java.time.OffsetDateTime;
import java.util.UUID;

@Data
@Builder
public class ZoneResponse {
    private UUID id;
    private String name;
    private Double centerLatitude;
    private Double centerLongitude;
    private Double radiusKm;
    private Boolean active;
    private OffsetDateTime createdAt;
    private OffsetDateTime updatedAt;
}
