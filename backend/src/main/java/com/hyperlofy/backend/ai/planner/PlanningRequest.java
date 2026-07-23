package com.hyperlofy.backend.ai.planner;

import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Size;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.util.UUID;

@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class PlanningRequest {
    @NotNull(message = "Customer id is required")
    private UUID customerId;
    private UUID conversationId;

    @Size(max = 2000, message = "Prompt must not exceed 2000 characters")
    private String prompt;

    private Double latitude;
    private Double longitude;

    private boolean requestRecommendations = true;
    private boolean requestMerchantSelection = true;
}
