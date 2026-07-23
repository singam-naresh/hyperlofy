package com.hyperlofy.backend.ai.orderbuilder;

import com.hyperlofy.backend.ai.conversation.ConversationResponse;
import com.hyperlofy.backend.ai.conversation.ConversationState;
import com.hyperlofy.backend.ai.memory.dto.MemoryDto;
import com.hyperlofy.backend.ai.memory.MemoryService;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;

import java.util.List;

@Slf4j
@Service
@RequiredArgsConstructor
public class OrderBuilderService {

    private final OrderDraftFactory orderDraftFactory;
    private final OrderDraftValidator orderDraftValidator;
    private final MemoryService memoryService;

    public OrderBuilderResponse build(ConversationResponse conversation) {
        long startedAt = System.currentTimeMillis();
        if (conversation == null || conversation.getConversationId() == null || conversation.getCustomerId() == null) {
            return OrderBuilderResponse.builder()
                    .success(false)
                    .message("Missing conversation context")
                    .validationResult(ValidationResult.builder().valid(false).errors(List.of("Missing conversation context")).build())
                    .build();
        }

        if (conversation.getState() == null || conversation.getState() != ConversationState.READY_FOR_ORDER) {
            return OrderBuilderResponse.builder()
                    .success(false)
                    .message("Conversation is not ready for order drafting")
                    .validationResult(ValidationResult.builder().valid(false).errors(List.of("Conversation is not ready for order drafting")).build())
                    .build();
        }

        try {
            OrderBuilderResponse factoryResponse = orderDraftFactory.build(conversation);
            OrderDraft draft = factoryResponse.getDraft();
            if (draft != null) {
                List<MemoryDto> memories = memoryService.findRelevantMemory(draft.getCustomerId(), draft);
                if (memories == null) {
                    memories = List.of();
                }
                if (draft.getMemoryPreferences() == null) {
                    draft.setMemoryPreferences(new java.util.HashMap<>());
                }
                draft.getMemoryPreferences().put("relevant_memories", memories);
            }
            ValidationResult validation = orderDraftValidator.validate(draft);

            if (!validation.isValid()) {
                return OrderBuilderResponse.builder()
                        .success(false)
                        .draft(draft)
                        .validationResult(validation)
                        .message("Draft validation failed")
                        .build();
            }

            draft.setValidation(validation);
            draft.setStatus("DRAFT_READY");
            draft.setMetadata(com.hyperlofy.backend.ai.orderbuilder.OrderMetadata.builder()
                    .source("AI_CONVERSATION")
                    .confidence(0.94)
                    .requiresPrescription(false)
                    .requiresVerification(false)
                    .merchantIndependent(true)
                    .processingStatus("VALIDATED")
                    .build());

            long duration = System.currentTimeMillis() - startedAt;
            log.info("Order draft built successfully. DraftId={}, ConversationId={}, Intent={}, Plan={}, ProcessingTime={}ms, Success=true",
                    draft.getDraftId(), draft.getConversationId(), draft.getIntent(), draft.getPlan(), duration);
            return OrderBuilderResponse.builder()
                    .success(true)
                    .draft(draft)
                    .validationResult(validation)
                    .message("Order draft created successfully")
                    .build();
        } catch (Exception ex) {
            long duration = System.currentTimeMillis() - startedAt;
            log.warn("Order draft building failed. ConversationId={}, Intent={}, Plan={}, ProcessingTime={}ms, Success=false",
                    conversation.getConversationId(), conversation.getIntent(), conversation.getPlan(), duration);
            return OrderBuilderResponse.builder()
                    .success(false)
                    .message("Builder failure")
                    .validationResult(ValidationResult.builder().valid(false).errors(List.of(ex.getMessage())).build())
                    .build();
        }
    }
}
