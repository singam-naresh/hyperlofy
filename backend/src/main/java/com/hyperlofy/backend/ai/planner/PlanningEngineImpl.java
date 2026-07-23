package com.hyperlofy.backend.ai.planner;

import com.hyperlofy.backend.ai.conversation.ConversationRequest;
import com.hyperlofy.backend.ai.conversation.ConversationResponse;
import com.hyperlofy.backend.ai.conversation.ConversationService;
import com.hyperlofy.backend.ai.conversation.ConversationState;
import com.hyperlofy.backend.ai.merchantselection.MerchantSelectionRequest;
import com.hyperlofy.backend.ai.merchantselection.MerchantSelectionResponse;
import com.hyperlofy.backend.ai.merchantselection.MerchantSelectionService;
import com.hyperlofy.backend.ai.orderbuilder.OrderBuilderResponse;
import com.hyperlofy.backend.ai.orderbuilder.OrderBuilderService;
import com.hyperlofy.backend.ai.recommendation.dto.RecommendationRequest;
import com.hyperlofy.backend.ai.recommendation.dto.RecommendationResponse;
import com.hyperlofy.backend.ai.recommendation.RecommendationService;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Component;

@Slf4j
@Component
@RequiredArgsConstructor
public class PlanningEngineImpl implements PlanningEngine {

    private final ConversationService conversationService;
    private final OrderBuilderService orderBuilderService;
    private final MerchantSelectionService merchantSelectionService;
    private final RecommendationService recommendationService;

    @Override
    public PlanningResponse orchestrate(PlanningRequest request) {
        long startedAt = System.currentTimeMillis();
        try {
            ConversationResponse conversationResponse = buildConversation(request);

            if (conversationResponse == null) {
                return PlanningResponse.builder()
                        .success(false)
                        .status(PlanningStatus.ERROR)
                        .message("Conversation orchestration failed")
                        .build();
            }

            PlanningResponse.PlanningResponseBuilder responseBuilder = PlanningResponse.builder()
                    .success(true)
                    .conversation(conversationResponse)
                    .message("Planning orchestration completed")
                    .status(resolveStatus(conversationResponse));

            if (conversationResponse.getState() == ConversationState.READY_FOR_ORDER) {
                OrderBuilderResponse draftResponse = orderBuilderService.build(conversationResponse);
                responseBuilder.orderDraft(draftResponse);
                if (draftResponse != null && draftResponse.isSuccess()) {
                    responseBuilder.status(PlanningStatus.ORDER_DRAFT_CREATED);
                }

                if (draftResponse != null && draftResponse.isSuccess() && request.getLatitude() != null && request.getLongitude() != null && request.isRequestMerchantSelection()) {
                    MerchantSelectionRequest merchantRequest = MerchantSelectionRequest.builder()
                            .requestId(java.util.UUID.randomUUID())
                            .draft(draftResponse.getDraft())
                            .latitude(request.getLatitude())
                            .longitude(request.getLongitude())
                            .customerPreferences(draftResponse.getDraft().getMemoryPreferences())
                            .selectionConstraints(java.util.Map.of())
                            .build();

                    MerchantSelectionResponse selectionResponse = merchantSelectionService.select(merchantRequest);
                    responseBuilder.merchantSelection(selectionResponse);
                    if (selectionResponse != null && selectionResponse.isSuccess()) {
                        responseBuilder.status(PlanningStatus.MERCHANT_SELECTED);
                    }
                }

                if (draftResponse != null && draftResponse.isSuccess() && request.isRequestRecommendations()) {
                    RecommendationRequest recommendationRequest = RecommendationRequest.builder()
                            .customerId(request.getCustomerId())
                            .conversationId(conversationResponse.getConversationId())
                            .orderDraftId(draftResponse.getDraft().getDraftId())
                            .prompt(request.getPrompt() == null ? "" : request.getPrompt())
                            .scenario(com.hyperlofy.backend.ai.recommendation.RecommendationType.MEMORY_BASED)
                            .build();
                    RecommendationResponse recommendationResponse = recommendationService.generateRecommendations(recommendationRequest, conversationResponse, responseBuilder.build().getMerchantSelection() == null ? java.util.List.of() : responseBuilder.build().getMerchantSelection().getPlan().getSelectedMerchants(), draftResponse.getDraft());
                    responseBuilder.recommendation(recommendationResponse);
                    if (recommendationResponse != null && recommendationResponse.getRecommendationId() != null) {
                        responseBuilder.status(PlanningStatus.RECOMMENDATION_PROVIDED);
                    }
                }
            }

            PlanningResponse response = responseBuilder.build();
            long duration = System.currentTimeMillis() - startedAt;
            log.info("Planning orchestration completed. CustomerId={}, ConversationId={}, Status={}, ExecutionTimeMs={}",
                    request.getCustomerId(), request.getConversationId(), response.getStatus(), duration);
            return response;
        } catch (Exception ex) {
            log.error("Planning orchestration failed for customer {}", request.getCustomerId(), ex);
            return PlanningResponse.builder()
                    .success(false)
                    .status(PlanningStatus.ERROR)
                    .message(ex.getMessage())
                    .build();
        }
    }

    private ConversationResponse buildConversation(PlanningRequest request) {
        if (request.getConversationId() != null) {
            ConversationRequest conversationRequest = ConversationRequest.builder()
                    .conversationId(request.getConversationId())
                    .customerId(request.getCustomerId())
                    .prompt(request.getPrompt())
                    .build();
            return conversationService.resume(request.getConversationId(), conversationRequest);
        }

        ConversationRequest conversationRequest = ConversationRequest.builder()
                .customerId(request.getCustomerId())
                .prompt(request.getPrompt())
                .build();
        return conversationService.process(conversationRequest);
    }

    private PlanningStatus resolveStatus(ConversationResponse conversationResponse) {
        if (conversationResponse == null) {
            return PlanningStatus.ERROR;
        }
        if (conversationResponse.getState() == ConversationState.READY_FOR_ORDER) {
            return PlanningStatus.ORDER_DRAFT_CREATED;
        }
        if (conversationResponse.getState() == ConversationState.WAITING_FOR_CUSTOMER || conversationResponse.getState() == ConversationState.COLLECTING_INFORMATION) {
            return PlanningStatus.CONVERSATION_IN_PROGRESS;
        }
        return PlanningStatus.WAITING_FOR_CUSTOMER;
    }
}
