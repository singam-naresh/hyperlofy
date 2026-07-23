package com.hyperlofy.backend.ai.conversation;

import org.springframework.stereotype.Component;

import java.util.Map;

@Component
public class HelperQuestionStrategy implements QuestionStrategy {

    @Override
    public boolean supports(String intent) {
        return intent != null && (intent.equalsIgnoreCase("DOCUMENT_DELIVERY") || intent.equalsIgnoreCase("PARCEL_DELIVERY") || intent.equalsIgnoreCase("ITEM_DELIVERY") || intent.equalsIgnoreCase("HELPER_REQUEST"));
    }

    @Override
    public ConversationQuestion nextQuestion(String intent, Map<String, Object> collectedEntities, Map<String, Object> missingEntities) {
        if (!collectedEntities.containsKey("pickup")) {
            return ConversationQuestion.builder().fieldName("pickup").question("What is the pickup address?").priority(1).build();
        }
        if (!collectedEntities.containsKey("drop")) {
            return ConversationQuestion.builder().fieldName("drop").question("What is the delivery address?").priority(2).build();
        }
        if (!collectedEntities.containsKey("recipientPhone")) {
            return ConversationQuestion.builder().fieldName("recipientPhone").question("Recipient phone number?").priority(3).build();
        }
        if (!collectedEntities.containsKey("fragile")) {
            return ConversationQuestion.builder().fieldName("fragile").question("Fragile item?").priority(4).build();
        }
        if (!collectedEntities.containsKey("otp")) {
            return ConversationQuestion.builder().fieldName("otp").question("Need OTP verification?").priority(5).build();
        }
        return ConversationQuestion.builder().fieldName("instructions").question("Any special delivery instructions?").priority(6).build();
    }
}
