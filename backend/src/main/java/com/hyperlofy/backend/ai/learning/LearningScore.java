package com.hyperlofy.backend.ai.learning;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

@Getter
@Setter
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class LearningScore {
    private double confidence;
    private double frequency;
    private double recency;
    private double decay;
    private double weightedScore;
}
