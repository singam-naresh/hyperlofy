package com.hyperlofy.backend.ai.orderbuilder;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class OrderMetadata {
    private String source;
    private double confidence;
    private boolean requiresPrescription;
    private boolean requiresVerification;
    private boolean merchantIndependent;
    private String processingStatus;
}
