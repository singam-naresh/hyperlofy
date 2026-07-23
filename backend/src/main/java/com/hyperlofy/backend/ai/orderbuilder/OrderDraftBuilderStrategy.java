package com.hyperlofy.backend.ai.orderbuilder;

import com.hyperlofy.backend.ai.conversation.ConversationResponse;

public interface OrderDraftBuilderStrategy {
    boolean supports(String intent, String plan);
    OrderBuilderResponse build(ConversationResponse conversation);
}
