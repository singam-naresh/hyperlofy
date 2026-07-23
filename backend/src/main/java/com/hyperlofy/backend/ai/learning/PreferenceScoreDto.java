package com.hyperlofy.backend.ai.learning;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class PreferenceScoreDto {
    private String preferenceKey;
    private double score;
    private double confidence;
    private double recency;
    private double frequency;
}
