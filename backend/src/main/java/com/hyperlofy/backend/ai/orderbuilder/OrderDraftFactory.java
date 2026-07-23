package com.hyperlofy.backend.ai.orderbuilder;

import com.hyperlofy.backend.ai.conversation.ConversationResponse;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Component;

import java.util.List;

@Component
@RequiredArgsConstructor
public class OrderDraftFactory {

    private final List<OrderDraftBuilderStrategy> strategies;

    public OrderBuilderResponse build(ConversationResponse conversation) {
        OrderDraftBuilderStrategy strategy = strategies.stream()
                .filter(item -> item.supports(conversation.getIntent(), conversation.getPlan()))
                .findFirst()
                .orElseThrow(() -> new IllegalArgumentException("No order draft strategy registered for intent=" + conversation.getIntent()));
        return strategy.build(conversation);
    }
}
