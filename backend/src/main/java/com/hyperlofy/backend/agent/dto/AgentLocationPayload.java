package com.hyperlofy.backend.agent.dto;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.util.UUID;

@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class AgentLocationPayload {
    private UUID agentId;
    private UUID orderId;
    private double latitude;
    private double longitude;
}
