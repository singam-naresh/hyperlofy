package com.hyperlofy.backend.ai.orderbuilder;

import com.hyperlofy.backend.ai.conversation.ConversationResponse;
import org.springframework.stereotype.Component;

import java.math.BigDecimal;
import java.util.ArrayList;
import java.util.List;
import java.util.Map;
import java.util.UUID;
import java.util.stream.Collectors;

@Component
public class ShoppingOrderDraftBuilderStrategy implements OrderDraftBuilderStrategy {

    @Override
    public boolean supports(String intent, String plan) {
        return intent != null && (intent.equals("GROCERY") || intent.equals("MEDICINE") || intent.equals("ELECTRONICS") || intent.equals("FOOD") || intent.equals("CAKE") || intent.equals("FLOWERS") || intent.equals("PET_SUPPLIES"));
    }

    @Override
    public OrderBuilderResponse build(ConversationResponse conversation) {
        Map<String, Object> entities = conversation.getCollectedEntities() == null ? Map.of() : conversation.getCollectedEntities();
        List<?> rawItems = (List<?>) entities.getOrDefault("items", List.of());
        List<OrderDraftItem> items = rawItems.stream()
                .map(item -> OrderDraftItem.builder()
                        .itemName(String.valueOf(item))
                        .category(String.valueOf(entities.getOrDefault("category", "General")))
                        .quantity(extractQuantity(entities))
                        .unit("unit")
                        .brand(null)
                        .estimatedPrice(BigDecimal.ZERO)
                        .substitutionsAllowed(true)
                        .specialInstructions(null)
                        .build())
                .collect(Collectors.toList());

        OrderDraft draft = OrderDraft.builder()
                .draftId(UUID.randomUUID())
                .conversationId(conversation.getConversationId())
                .customerId(conversation.getCustomerId())
                .plan(conversation.getPlan())
                .intent(conversation.getIntent())
                .orderType("SHOPPING")
                .status("DRAFT_PENDING")
                .items(items)
                .deliveryDetails(null)
                .recipient(null)
                .build();

        return OrderBuilderResponse.builder()
                .success(true)
                .draft(draft)
                .message("Shopping draft created")
                .build();
    }

    private int extractQuantity(Map<String, Object> entities) {
        Object quantity = entities.get("quantity");
        if (quantity instanceof Number) {
            return ((Number) quantity).intValue();
        }
        return 1;
    }
}
