package com.hyperlofy.backend.ai.conversation;

import org.springframework.stereotype.Component;

import java.time.OffsetDateTime;
import java.util.HashMap;
import java.util.Map;
import java.util.UUID;
import java.util.concurrent.ConcurrentHashMap;

@Component
public class ConversationManager {

    private final Map<UUID, ConversationContext> activeByCustomer = new ConcurrentHashMap<>();
    private final Map<UUID, ConversationContext> conversationStore = new ConcurrentHashMap<>();

    public ConversationContext startConversation(UUID customerId, String intent, String plan) {
        ConversationContext existing = activeByCustomer.get(customerId);
        if (existing != null) {
            existing.setState(ConversationState.CANCELLED);
            conversationStore.put(existing.getConversationId(), existing);
        }

        ConversationContext context = new ConversationContext();
        context.setConversationId(UUID.randomUUID());
        context.setCustomerId(customerId);
        context.setIntent(intent);
        context.setPlan(plan);
        context.setState(ConversationState.STARTED);
        context.setCollectedEntities(new HashMap<>());
        context.setMissingEntities(new HashMap<>());
        context.setStartedAt(OffsetDateTime.now());
        context.setUpdatedAt(OffsetDateTime.now());
        context.setCompletionPercentage(0.0);

        activeByCustomer.put(customerId, context);
        conversationStore.put(context.getConversationId(), context);
        return context;
    }

    public ConversationContext updateContext(ConversationContext context, String intent, Map<String, Object> entities) {
        Map<String, Object> safeEntities = entities == null ? new HashMap<>() : entities;
        context.setIntent(intent);
        context.setState(ConversationState.COLLECTING_INFORMATION);
        context.setCollectedEntities(safeEntities);
        context.setUpdatedAt(OffsetDateTime.now());
        context.setCompletionPercentage(Math.min(1.0, safeEntities.size() / 5.0));
        conversationStore.put(context.getConversationId(), context);
        return context;
    }

    public ConversationContext resumeConversation(UUID conversationId) {
        ConversationContext context = conversationStore.get(conversationId);
        if (context == null) {
            context = new ConversationContext();
            context.setConversationId(conversationId);
            context.setState(ConversationState.WAITING_FOR_CUSTOMER);
            context.setCollectedEntities(new HashMap<>());
            context.setMissingEntities(new HashMap<>());
            context.setStartedAt(OffsetDateTime.now());
            context.setUpdatedAt(OffsetDateTime.now());
        }
        context.setState(ConversationState.WAITING_FOR_CUSTOMER);
        context.setUpdatedAt(OffsetDateTime.now());
        conversationStore.put(context.getConversationId(), context);
        return context;
    }
}
