package com.hyperlofy.backend.ai.conversation;

import java.util.Map;

public interface QuestionStrategy {
    ConversationQuestion nextQuestion(String intent, Map<String, Object> collectedEntities, Map<String, Object> missingEntities);
    boolean supports(String intent);
}
