package com.hyperlofy.backend.ai.conversation;

public enum ConversationState {
    STARTED,
    COLLECTING_INFORMATION,
    WAITING_FOR_CUSTOMER,
    VALIDATING,
    READY_FOR_ORDER,
    COMPLETED,
    CANCELLED,
    TIMEOUT
}
