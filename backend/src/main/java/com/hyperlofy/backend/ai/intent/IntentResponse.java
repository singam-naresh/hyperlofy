package com.hyperlofy.backend.ai.intent;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.time.OffsetDateTime;
import java.util.Map;

@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class IntentResponse {

    private IntentType intent;
    private PlanType plan;
    private double confidence;
    private boolean requiresConversation;
    private boolean requiresVerification;
    private boolean requiresPrescription;
    private Map<String, Object> entities;
    private String nextAction;
    private String message;
    private OffsetDateTime timestamp;
}
