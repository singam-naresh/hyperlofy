package com.hyperlofy.backend.ai.planner;

import com.hyperlofy.backend.common.exception.BusinessException;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.stereotype.Service;
import org.springframework.util.StringUtils;

@Service
@RequiredArgsConstructor
public class PlanningService {

    private final PlanningEngine planningEngine;

    public PlanningResponse orchestrate(PlanningRequest request) {
        validateRequest(request);
        return planningEngine.orchestrate(request);
    }

    private void validateRequest(PlanningRequest request) {
        if (request == null) {
            throw new BusinessException("Planning request cannot be null", HttpStatus.BAD_REQUEST);
        }
        if (request.getCustomerId() == null) {
            throw new BusinessException("Customer id is required", HttpStatus.BAD_REQUEST);
        }
        if (request.getConversationId() == null && !StringUtils.hasText(request.getPrompt())) {
            throw new BusinessException("Prompt is required when starting a new conversation", HttpStatus.BAD_REQUEST);
        }
    }
}
