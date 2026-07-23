package com.hyperlofy.backend.ai.conversation;

import org.springframework.stereotype.Component;

@Component
public class ConversationStateMachine {

    public ConversationState transition(ConversationState currentState, boolean readyForOrder, boolean timeout) {
        if (timeout) {
            return ConversationState.TIMEOUT;
        }
        if (currentState == ConversationState.CANCELLED) {
            return ConversationState.CANCELLED;
        }
        if (readyForOrder) {
            return ConversationState.READY_FOR_ORDER;
        }
        if (currentState == ConversationState.STARTED) {
            return ConversationState.COLLECTING_INFORMATION;
        }
        if (currentState == ConversationState.COLLECTING_INFORMATION) {
            return ConversationState.COLLECTING_INFORMATION;
        }
        return ConversationState.WAITING_FOR_CUSTOMER;
    }
}
