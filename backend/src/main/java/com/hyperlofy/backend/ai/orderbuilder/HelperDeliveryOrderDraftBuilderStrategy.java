package com.hyperlofy.backend.ai.orderbuilder;

import com.hyperlofy.backend.ai.conversation.ConversationResponse;
import org.springframework.stereotype.Component;

import java.util.Map;
import java.util.UUID;

@Component
public class HelperDeliveryOrderDraftBuilderStrategy implements OrderDraftBuilderStrategy {

    @Override
    public boolean supports(String intent, String plan) {
        return intent != null && (intent.equals("DOCUMENT_DELIVERY") || intent.equals("PARCEL_DELIVERY") || intent.equals("ITEM_DELIVERY") || intent.equals("HELPER_REQUEST"));
    }

    @Override
    public OrderBuilderResponse build(ConversationResponse conversation) {
        Map<String, Object> entities = conversation.getCollectedEntities() == null ? Map.of() : conversation.getCollectedEntities();
        DeliveryDraft delivery = DeliveryDraft.builder()
                .pickup(String.valueOf(entities.getOrDefault("pickup", "")))
                .drop(String.valueOf(entities.getOrDefault("drop", "")))
                .schedule(String.valueOf(entities.getOrDefault("schedule", "IMMEDIATE")))
                .immediate(true)
                .recipient(String.valueOf(entities.getOrDefault("recipient", "")))
                .phone(String.valueOf(entities.getOrDefault("recipientPhone", "")))
                .instructions(String.valueOf(entities.getOrDefault("instructions", "")))
                .otp(Boolean.TRUE.equals(entities.get("otp")))
                .fragile(Boolean.TRUE.equals(entities.get("fragile")))
                .build();

        OrderDraft draft = OrderDraft.builder()
                .draftId(UUID.randomUUID())
                .conversationId(conversation.getConversationId())
                .customerId(conversation.getCustomerId())
                .plan(conversation.getPlan())
                .intent(conversation.getIntent())
                .orderType(conversation.getIntent())
                .status("DRAFT_PENDING")
                .items(java.util.List.of())
                .deliveryDetails(delivery)
                .recipient(RecipientDraft.builder()
                        .name(String.valueOf(entities.getOrDefault("recipient", "")))
                        .phone(String.valueOf(entities.getOrDefault("recipientPhone", "")))
                        .build())
                .build();

        return OrderBuilderResponse.builder()
                .success(true)
                .draft(draft)
                .message("Helper delivery draft created")
                .build();
    }
}
