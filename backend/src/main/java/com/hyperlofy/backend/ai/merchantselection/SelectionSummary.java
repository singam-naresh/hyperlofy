package com.hyperlofy.backend.ai.merchantselection;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class SelectionSummary {
    private int candidateCount;
    private int selectedCount;
    private String selectionType;
    private double averageScore;
}
