package com.hyperlofy.backend.ai.planner.fulfillment;

import com.hyperlofy.backend.common.exception.BusinessException;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.stereotype.Service;

@Service
@RequiredArgsConstructor
public class FulfillmentService {

    private final FulfillmentEngine fulfillmentEngine;

    public FulfillmentResponse fulfill(FulfillmentRequest request) {
        validateRequest(request);
        return fulfillmentEngine.orchestrate(request);
    }

    private void validateRequest(FulfillmentRequest request) {
        if (request == null) {
            throw new BusinessException("Fulfillment request cannot be null", HttpStatus.BAD_REQUEST);
        }
        if (request.getPlanningResponse() == null) {
            throw new BusinessException("Planning response is required", HttpStatus.BAD_REQUEST);
        }
        if (request.getPlanningResponse().getConversation() == null) {
            throw new BusinessException("Conversation context is required for fulfillment", HttpStatus.BAD_REQUEST);
        }
    }
}
