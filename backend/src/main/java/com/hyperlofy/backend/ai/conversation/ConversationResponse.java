package com.hyperlofy.backend.ai.conversation;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.time.OffsetDateTime;
import java.util.Map;
import java.util.UUID;

@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class ConversationResponse {
    private UUID conversationId;
    private UUID customerId;
    private String intent;
    private String plan;
    private ConversationState state;
    private String question;
    private Map<String, Object> collectedEntities;
    private Map<String, Object> missingEntities;
    private double completionPercentage;
    private boolean readyForOrder;
    private OffsetDateTime startedAt;
    private OffsetDateTime updatedAt;
    private String message;
}
