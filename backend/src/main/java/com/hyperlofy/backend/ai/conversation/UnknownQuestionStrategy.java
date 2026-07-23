package com.hyperlofy.backend.ai.conversation;

import java.util.Map;

public class UnknownQuestionStrategy implements QuestionStrategy {

    @Override
    public boolean supports(String intent) {
        return true;
    }

    @Override
    public ConversationQuestion nextQuestion(String intent, Map<String, Object> collectedEntities, Map<String, Object> missingEntities) {
        return ConversationQuestion.builder()
                .fieldName("clarification")
                .question("I need a bit more detail to continue. Please share the item or service you want help with.")
                .priority(1)
                .build();
    }
}
