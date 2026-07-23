package com.hyperlofy.backend.ai.conversation;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.time.OffsetDateTime;
import java.util.HashMap;
import java.util.Map;
import java.util.UUID;

@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class ConversationContext {
    private UUID conversationId;
    private UUID customerId;
    private String intent;
    private String plan;
    private ConversationState state;
    private Map<String, Object> collectedEntities = new HashMap<>();
    private Map<String, Object> missingEntities = new HashMap<>();
    private String lastQuestion;
    private OffsetDateTime startedAt;
    private OffsetDateTime updatedAt;
    private double completionPercentage;
}