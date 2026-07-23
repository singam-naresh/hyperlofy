package com.hyperlofy.backend.ai.conversation;

import com.hyperlofy.backend.ai.intent.IntentRequest;
import com.hyperlofy.backend.ai.intent.IntentResponse;
import com.hyperlofy.backend.ai.intent.IntentEngineService;
import com.hyperlofy.backend.ai.memory.MemoryExtractor;
import com.hyperlofy.backend.ai.memory.dto.MemoryCreateRequest;
import com.hyperlofy.backend.ai.memory.MemoryService;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

import java.time.OffsetDateTime;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.UUID;

@Service
@RequiredArgsConstructor
public class ConversationService {

    private final ConversationManager conversationManager;
    private final ConversationStateMachine conversationStateMachine;
    private final List<QuestionStrategy> questionStrategies;
    private final IntentEngineService intentEngineService;
    private final MemoryExtractor memoryExtractor;
    private final MemoryService memoryService;

    public ConversationResponse process(ConversationRequest request) {
        IntentResponse intentResponse = intentEngineService.classify(IntentRequest.builder().prompt(request.getPrompt()).build());

        ConversationContext context = conversationManager.startConversation(request.getCustomerId(), intentResponse.getIntent().name(), intentResponse.getPlan().name());
        Map<String, Object> entities = extractEntities(request.getPrompt());
        ConversationContext updated = conversationManager.updateContext(context, intentResponse.getIntent().name(), entities);

        List<MemoryCreateRequest> extractedMemories = memoryExtractor.extractMemoriesFromContext(updated);
        extractedMemories.forEach(memory -> memoryService.saveMemory(request.getCustomerId(), memory));

        QuestionStrategy strategy = questionStrategies.stream()
                .filter(s -> s.supports(updated.getIntent()))
                .findFirst()
                .orElse(new UnknownQuestionStrategy());

        ConversationQuestion question = strategy.nextQuestion(updated.getIntent(), updated.getCollectedEntities(), updated.getMissingEntities());
        updated.setLastQuestion(question.getQuestion());
        updated.setState(conversationStateMachine.transition(updated.getState(), false, false));
        updated.setUpdatedAt(OffsetDateTime.now());

        return ConversationResponse.builder()
                .conversationId(updated.getConversationId())
                .customerId(updated.getCustomerId())
                .intent(updated.getIntent())
                .plan(updated.getPlan())
                .state(updated.getState())
                .question(question.getQuestion())
                .collectedEntities(updated.getCollectedEntities())
                .missingEntities(updated.getMissingEntities())
                .completionPercentage(updated.getCompletionPercentage())
                .readyForOrder(false)
                .startedAt(updated.getStartedAt())
                .updatedAt(updated.getUpdatedAt())
                .message("Please answer the next question to continue the Hyperlofy request.")
                .build();
    }

    public ConversationResponse resume(UUID conversationId, ConversationRequest request) {
        ConversationContext context = conversationManager.resumeConversation(conversationId);
        return ConversationResponse.builder()
                .conversationId(context.getConversationId())
                .customerId(context.getCustomerId())
                .state(context.getState())
                .question("What is the next missing detail?")
                .collectedEntities(context.getCollectedEntities())
                .missingEntities(context.getMissingEntities())
                .completionPercentage(context.getCompletionPercentage())
                .readyForOrder(false)
                .startedAt(context.getStartedAt())
                .updatedAt(context.getUpdatedAt())
                .message("Conversation resumed.")
                .build();
    }

    private Map<String, Object> extractEntities(String prompt) {
        Map<String, Object> entities = new HashMap<>();
        String normalized = prompt == null ? "" : prompt.toLowerCase();
        if (normalized.contains("grocery") || normalized.contains("groceries")) {
            entities.put("item", "groceries");
        }
        if (normalized.contains("deliver") || normalized.contains("documents") || normalized.contains("parcel")) {
            entities.put("drop", "delivery address pending");
        }
        if (normalized.contains("organic")) {
            entities.put("food_preference", "organic vegetables");
        }
        if (normalized.contains("dmart")) {
            entities.put("brand", "dmart");
        }
        if (normalized.contains("milk") && normalized.contains("sunday")) {
            entities.put("shopping_pattern", "milk every Sunday");
        }
        if (normalized.contains("after 8 pm") || normalized.contains("after 8pm") || normalized.contains("evening delivery")) {
            entities.put("delivery_time", "after 8 PM");
        }
        if (normalized.contains("hate spicy") || normalized.contains("no spicy")) {
            entities.put("allergy", "spicy");
        }
        return entities;
    }
}
