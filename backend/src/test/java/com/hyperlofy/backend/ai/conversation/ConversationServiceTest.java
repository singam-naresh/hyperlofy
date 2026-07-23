package com.hyperlofy.backend.ai.conversation;

import com.hyperlofy.backend.ai.intent.IntentEngineService;
import com.hyperlofy.backend.ai.memory.MemoryExtractor;
import com.hyperlofy.backend.ai.memory.MemoryService;
import org.junit.jupiter.api.Test;
import org.mockito.Mockito;

import java.util.List;
import java.util.UUID;

import static org.junit.jupiter.api.Assertions.*;

class ConversationServiceTest {

    private final ConversationManager conversationManager = new ConversationManager();
    private final ConversationStateMachine stateMachine = new ConversationStateMachine();
    private final IntentEngineService intentEngineService = new IntentEngineService();
    private final MemoryExtractor memoryExtractor = new MemoryExtractor();
    private final MemoryService memoryService = Mockito.mock(MemoryService.class);
    private final List<QuestionStrategy> strategies = List.of(new ShoppingQuestionStrategy(), new HelperQuestionStrategy());
    private final ConversationService conversationService = new ConversationService(conversationManager, stateMachine, strategies, intentEngineService, memoryExtractor, memoryService);

    @Test
    void startShoppingConversationAsksQuestion() {
        ConversationResponse response = conversationService.process(ConversationRequest.builder()
                .customerId(UUID.randomUUID())
                .prompt("I need groceries")
                .build());

        assertNotNull(response.getConversationId());
        assertNotNull(response.getQuestion());
        assertEquals(ConversationState.COLLECTING_INFORMATION, response.getState());
    }

    @Test
    void startHelperConversationAsksPickupAddress() {
        ConversationResponse response = conversationService.process(ConversationRequest.builder()
                .customerId(UUID.randomUUID())
                .prompt("Deliver my laptop")
                .build());

        assertNotNull(response.getQuestion());
        assertTrue(response.getQuestion().toLowerCase().contains("pickup address"));
    }

    @Test
    void topicSwitchStartsFreshConversation() {
        UUID customerId = UUID.randomUUID();
        ConversationResponse first = conversationService.process(ConversationRequest.builder()
                .customerId(customerId)
                .prompt("I need groceries")
                .build());

        ConversationResponse second = conversationService.process(ConversationRequest.builder()
                .customerId(customerId)
                .prompt("Actually deliver my laptop")
                .build());

        assertNotEquals(first.getConversationId(), second.getConversationId());
    }

    @Test
    void updateContextWithNullEntitiesDoesNotThrow() {
        UUID customerId = UUID.randomUUID();
        ConversationResponse response = conversationService.process(ConversationRequest.builder()
                .customerId(customerId)
                .prompt("I need groceries")
                .build());

        ConversationContext updated = conversationManager.updateContext(
                conversationManager.startConversation(customerId, "GROCERY", "AI_SHOPPING_CONCIERGE"),
                "GROCERY",
                null
        );

        assertNotNull(updated);
        assertEquals(ConversationState.COLLECTING_INFORMATION, updated.getState());
        assertNotNull(updated.getCollectedEntities());
        assertEquals(0.0, updated.getCompletionPercentage());
    }

    @Test
    void resumeConversationReturnsWaitingState() {
        UUID customerId = UUID.randomUUID();
        ConversationResponse response = conversationService.process(ConversationRequest.builder()
                .customerId(customerId)
                .prompt("I need groceries")
                .build());

        ConversationResponse resumed = conversationService.resume(response.getConversationId(), ConversationRequest.builder()
                .customerId(customerId)
                .prompt("I need milk")
                .build());

        assertEquals(ConversationState.WAITING_FOR_CUSTOMER, resumed.getState());
    }

    @Test
    void spamPromptRejectedByConversationService() {
        ConversationResponse response = conversationService.process(ConversationRequest.builder()
                .customerId(UUID.randomUUID())
                .prompt("asdfasdf")
                .build());

        assertNotNull(response.getQuestion());
    }
}
