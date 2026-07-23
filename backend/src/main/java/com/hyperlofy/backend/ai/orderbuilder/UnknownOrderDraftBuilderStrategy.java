package com.hyperlofy.backend.ai.orderbuilder;

import com.hyperlofy.backend.ai.conversation.ConversationResponse;
import org.springframework.stereotype.Component;

import java.util.UUID;

@Component
public class UnknownOrderDraftBuilderStrategy implements OrderDraftBuilderStrategy {

    @Override
    public boolean supports(String intent, String plan) {
        return true;
    }

    @Override
    public OrderBuilderResponse build(ConversationResponse conversation) {
        OrderDraft draft = OrderDraft.builder()
                .draftId(UUID.randomUUID())
                .conversationId(conversation.getConversationId())
                .customerId(conversation.getCustomerId())
                .plan(conversation.getPlan())
                .intent(conversation.getIntent())
                .orderType("UNKNOWN")
                .status("DRAFT_REJECTED")
                .build();

        return OrderBuilderResponse.builder()
                .success(false)
                .draft(draft)
                .message("Unknown conversation intent")
                .build();
    }
}
