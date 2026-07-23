package com.hyperlofy.backend.ai.learning;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.util.UUID;

@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class LearningResponse {
    private UUID learningId;
    private UUID customerId;
    private LearningType learningType;
    private double score;
    private double confidence;
    private double recency;
    private double frequency;
    private String details;
}
