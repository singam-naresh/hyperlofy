package com.hyperlofy.backend.ai.planner;

import com.hyperlofy.backend.common.exception.BusinessException;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import java.util.UUID;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertNotNull;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.mockito.Mockito.when;

@ExtendWith(MockitoExtension.class)
class PlanningServiceTest {

    @Mock
    private PlanningEngine planningEngine;

    @InjectMocks
    private PlanningService planningService;

    @Test
    void shouldDelegateOrchestrationToEngine() {
        UUID customerId = UUID.randomUUID();
        PlanningRequest request = PlanningRequest.builder()
                .customerId(customerId)
                .prompt("Get me groceries and delivery.")
                .build();

        PlanningResponse expected = PlanningResponse.builder()
                .success(true)
                .status(PlanningStatus.CONVERSATION_IN_PROGRESS)
                .message("OK")
                .build();

        when(planningEngine.orchestrate(request)).thenReturn(expected);

        PlanningResponse actual = planningService.orchestrate(request);

        assertNotNull(actual);
        assertEquals(expected.isSuccess(), actual.isSuccess());
        assertEquals(expected.getStatus(), actual.getStatus());
        assertEquals(expected.getMessage(), actual.getMessage());
    }

    @Test
    void shouldRejectRequestWithoutPromptOrConversationId() {
        PlanningRequest request = PlanningRequest.builder()
                .customerId(UUID.randomUUID())
                .build();

        assertThrows(BusinessException.class, () -> planningService.orchestrate(request));
    }
}
