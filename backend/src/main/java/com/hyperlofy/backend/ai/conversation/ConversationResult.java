package com.hyperlofy.backend.ai.conversation;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.util.Map;

@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class ConversationResult {
    private boolean readyForOrder;
    private Map<String, Object> entities;
    private String message;
}
