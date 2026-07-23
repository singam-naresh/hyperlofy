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
public class ConversationSession {
    private UUID conversationId;
    private UUID customerId;
    private String intent;
    private String plan;
    private ConversationState state;
    private Map<String, Object> collectedEntities;
    private Map<String, Object> missingEntities;
    private String lastQuestion;
    private OffsetDateTime startedAt;
    private OffsetDateTime updatedAt;
    private double completionPercentage;
}
