package com.hyperlofy.backend.ai.orderbuilder;

import com.hyperlofy.backend.ai.conversation.ConversationResponse;
import com.hyperlofy.backend.ai.conversation.ConversationState;
import com.hyperlofy.backend.ai.intent.IntentType;
import com.hyperlofy.backend.ai.intent.PlanType;
import com.hyperlofy.backend.ai.memory.MemoryService;
import org.junit.jupiter.api.Test;
import org.mockito.Mockito;

import java.util.List;
import java.util.Map;
import java.util.UUID;

import static org.junit.jupiter.api.Assertions.*;

class OrderBuilderServiceTest {

    private final OrderDraftFactory orderDraftFactory = new OrderDraftFactory(List.of(
            new ShoppingOrderDraftBuilderStrategy(),
            new HelperDeliveryOrderDraftBuilderStrategy(),
            new UnknownOrderDraftBuilderStrategy()));
    private final OrderDraftValidator orderDraftValidator = new OrderDraftValidator();
    private final MemoryService memoryService = Mockito.mock(MemoryService.class);
    private final OrderBuilderService orderBuilderService = new OrderBuilderService(orderDraftFactory, orderDraftValidator, memoryService);

    @Test
    void buildsShoppingDraftFromConversation() {
        ConversationResponse conversation = ConversationResponse.builder()
                .conversationId(UUID.randomUUID())
                .customerId(UUID.randomUUID())
                .intent(IntentType.GROCERY.name())
                .plan(PlanType.AI_SHOPPING_CONCIERGE.name())
                .state(ConversationState.READY_FOR_ORDER)
                .collectedEntities(Map.of(
                        "items", List.of("Rice", "Curd", "Vegetables", "Oil"),
                        "quantity", 5,
                        "category", "Groceries"
                ))
                .build();

        OrderBuilderResponse response = orderBuilderService.build(conversation);

        assertTrue(response.isSuccess());
        assertNotNull(response.getDraft());
        assertEquals("GROCERY", response.getDraft().getIntent());
        assertEquals(4, response.getDraft().getItems().size());
    }

    @Test
    void buildsHelperDeliveryDraftFromConversation() {
        ConversationResponse conversation = ConversationResponse.builder()
                .conversationId(UUID.randomUUID())
                .customerId(UUID.randomUUID())
                .intent(IntentType.ITEM_DELIVERY.name())
                .plan(PlanType.AI_HELPER_CONCIERGE.name())
                .state(ConversationState.READY_FOR_ORDER)
                .collectedEntities(Map.of(
                        "pickup", "HSR Layout",
                        "drop", "Koramangala",
                        "recipient", "Asha",
                        "recipientPhone", "9876543210",
                        "fragile", true,
                        "otp", true,
                        "instructions", "Handle with care"
                ))
                .build();

        OrderBuilderResponse response = orderBuilderService.build(conversation);

        assertTrue(response.isSuccess());
        assertNotNull(response.getDraft().getDeliveryDetails());
        assertEquals("ITEM_DELIVERY", response.getDraft().getOrderType());
    }

    @Test
    void rejectsDuplicateItems() {
        ConversationResponse conversation = ConversationResponse.builder()
                .conversationId(UUID.randomUUID())
                .customerId(UUID.randomUUID())
                .intent(IntentType.GROCERY.name())
                .plan(PlanType.AI_SHOPPING_CONCIERGE.name())
                .state(ConversationState.READY_FOR_ORDER)
                .collectedEntities(Map.of(
                        "items", List.of("Rice", "Rice"),
                        "quantity", 2,
                        "category", "Groceries"
                ))
                .build();

        OrderBuilderResponse response = orderBuilderService.build(conversation);

        assertFalse(response.isSuccess());
        assertNotNull(response.getValidationResult());
    }

    @Test
    void rejectsMissingPhoneForDelivery() {
        ConversationResponse conversation = ConversationResponse.builder()
                .conversationId(UUID.randomUUID())
                .customerId(UUID.randomUUID())
                .intent(IntentType.ITEM_DELIVERY.name())
                .plan(PlanType.AI_HELPER_CONCIERGE.name())
                .state(ConversationState.READY_FOR_ORDER)
                .collectedEntities(Map.of(
                        "pickup", "HSR Layout",
                        "drop", "Koramangala",
                        "recipient", "Asha"
                ))
                .build();

        OrderBuilderResponse response = orderBuilderService.build(conversation);

        assertFalse(response.isSuccess());
    }
}
