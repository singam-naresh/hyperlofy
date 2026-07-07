package com.hyperlofy.backend.agent.dto;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.time.OffsetDateTime;
import java.util.UUID;

@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class LiveTrackingResponse {
    private UUID orderId;
    private UUID agentId;
    private UUID customerId;
    private double latitude;
    private double longitude;
    private double distanceRemainingKm;
    private double etaMinutes;
    private String orderStatus;
    private OffsetDateTime timestamp;
}
