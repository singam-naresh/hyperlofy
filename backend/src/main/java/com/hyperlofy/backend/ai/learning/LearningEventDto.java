package com.hyperlofy.backend.ai.learning;

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
public class LearningEventDto {

    @NotNull
    private UUID customerId;

    private UUID conversationId;

    private UUID orderId;

    private UUID merchantId;

    private UUID recommendationId;

    @NotNull
    private LearningType learningType;

    private Double confidence;

    private Double frequency;

    private Double recency;

    private String details;
}
