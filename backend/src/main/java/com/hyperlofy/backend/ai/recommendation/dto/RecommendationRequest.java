package com.hyperlofy.backend.ai.recommendation.dto;

import com.hyperlofy.backend.ai.recommendation.RecommendationType;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.util.UUID;

@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class RecommendationRequest {

    @NotNull
    private UUID customerId;

    @NotNull
    private UUID conversationId;

    @NotNull
    private UUID orderDraftId;

    @NotBlank
    private String prompt;

    private RecommendationType scenario;
}
