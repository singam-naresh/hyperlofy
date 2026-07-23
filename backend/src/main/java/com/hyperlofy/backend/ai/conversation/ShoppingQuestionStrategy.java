package com.hyperlofy.backend.ai.conversation;

import org.springframework.stereotype.Component;

import java.util.Map;

@Component
public class ShoppingQuestionStrategy implements QuestionStrategy {

    @Override
    public boolean supports(String intent) {
        return intent != null && intent.startsWith("GROCERY") || intent != null && intent.startsWith("FOOD") || intent != null && intent.startsWith("CAKE") || intent != null && intent.startsWith("FLOWERS") || intent != null && intent.startsWith("PET_SUPPLIES") || intent != null && intent.startsWith("ELECTRONICS");
    }

    @Override
    public ConversationQuestion nextQuestion(String intent, Map<String, Object> collectedEntities, Map<String, Object> missingEntities) {
        if (!collectedEntities.containsKey("item")) {
            return ConversationQuestion.builder().fieldName("item").question("What are you planning to cook or what groceries do you need?").priority(1).build();
        }
        if (!collectedEntities.containsKey("quantity") && intent.equalsIgnoreCase("GROCERY")) {
            return ConversationQuestion.builder().fieldName("quantity").question("How many units do you need?").priority(2).build();
        }
        if (!collectedEntities.containsKey("flavour") && intent.equalsIgnoreCase("CAKE")) {
            return ConversationQuestion.builder().fieldName("flavour").question("What flavour would you like?").priority(2).build();
        }
        return ConversationQuestion.builder().fieldName("deliveryTime").question("When would you like it delivered?").priority(3).build();
    }
}
