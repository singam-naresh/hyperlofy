package com.hyperlofy.backend.ai.recommendation;

import com.hyperlofy.backend.ai.conversation.ConversationResponse;
import com.hyperlofy.backend.ai.memory.MemoryService;
import com.hyperlofy.backend.ai.orderbuilder.OrderDraft;
import com.hyperlofy.backend.ai.recommendation.dto.RecommendationActionRequest;
import com.hyperlofy.backend.ai.recommendation.dto.RecommendationRequest;
import com.hyperlofy.backend.ai.recommendation.dto.RecommendationResponse;
import com.hyperlofy.backend.ai.recommendation.repository.RecommendationRepository;
import com.hyperlofy.backend.common.exception.BusinessException;
import com.hyperlofy.backend.order.repository.OrderRepository;
import com.hyperlofy.backend.user.entity.Role;
import com.hyperlofy.backend.user.entity.User;
import com.hyperlofy.backend.user.repository.UserRepository;
import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.Mockito;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.context.SecurityContext;
import org.springframework.security.core.context.SecurityContextHolder;

import java.util.List;
import java.util.Optional;
import java.util.UUID;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.when;

@ExtendWith(MockitoExtension.class)
class RecommendationServiceTest {

    @Mock
    private RecommendationRepository recommendationRepository;

    @Mock
    private RecommendationMapper recommendationMapper;

    @Mock
    private RecommendationGenerator recommendationGenerator;

    @Mock
    private RecommendationRanker recommendationRanker;

    @Mock
    private RecommendationContextBuilder contextBuilder;

    @Mock
    private MemoryService memoryService;

    @Mock
    private OrderRepository orderRepository;

    @Mock
    private UserRepository userRepository;

    @InjectMocks
    private RecommendationService recommendationService;

    @AfterEach
    void tearDown() {
        SecurityContextHolder.clearContext();
    }

    @Test
    void shouldFetchRecommendationsForAuthorizedCustomer() {
        UUID customerId = UUID.randomUUID();
        User user = User.builder()
                .email("customer@example.com")
                .role(Role.CUSTOMER)
                .build();
        user.setId(customerId);

        RecommendationEntity entity = RecommendationEntity.builder()
                .recommendationId(UUID.randomUUID())
                .customerId(customerId)
                .recommendedItem("Milk")
                .reason(RecommendationReason.FREQUENCY)
                .recommendationType(RecommendationType.FREQUENT_PURCHASE)
                .score(0.8)
                .accepted(false)
                .dismissed(false)
                .build();

        RecommendationResponse response = RecommendationResponse.builder()
                .recommendationId(entity.getRecommendationId())
                .customerId(customerId)
                .recommendedItem(entity.getRecommendedItem())
                .reason(entity.getReason())
                .recommendationType(entity.getRecommendationType())
                .score(entity.getScore())
                .build();

        setAuthenticatedUser(user.getEmail());
        when(userRepository.findByEmail(user.getEmail())).thenReturn(Optional.of(user));
        when(recommendationRepository.findByCustomerIdAndDismissedFalse(customerId)).thenReturn(List.of(entity));
        when(recommendationMapper.toDto(entity)).thenReturn(response);

        List<RecommendationResponse> result = recommendationService.fetchRecommendations(customerId);

        assertNotNull(result);
        assertEquals(1, result.size());
        assertEquals("Milk", result.get(0).getRecommendedItem());
    }

    @Test
    void shouldRejectFetchRecommendationsForOtherCustomer() {
        UUID customerId = UUID.randomUUID();
        UUID otherCustomerId = UUID.randomUUID();
        User user = User.builder()
                .email("other@example.com")
                .role(Role.CUSTOMER)
                .build();
        user.setId(otherCustomerId);

        setAuthenticatedUser(user.getEmail());
        when(userRepository.findByEmail(user.getEmail())).thenReturn(Optional.of(user));

        BusinessException exception = assertThrows(BusinessException.class,
                () -> recommendationService.fetchRecommendations(customerId));

        assertEquals(403, exception.getStatus().value());
    }

    @Test
    void shouldGenerateRecommendationsWhenAuthorized() {
        UUID customerId = UUID.randomUUID();
        UUID conversationId = UUID.randomUUID();
        UUID orderDraftId = UUID.randomUUID();

        User user = User.builder()
                .email("customer@example.com")
                .role(Role.CUSTOMER)
                .build();
        user.setId(customerId);

        RecommendationRequest request = RecommendationRequest.builder()
                .customerId(customerId)
                .conversationId(conversationId)
                .orderDraftId(orderDraftId)
                .prompt("Recommend groceries for dinner")
                .scenario(RecommendationType.COMPLEMENTARY_PRODUCT)
                .build();

        RecommendationInput input = RecommendationInput.builder()
                .customerId(customerId)
                .conversationId(conversationId)
                .orderDraftId(orderDraftId)
                .prompt(request.getPrompt())
                .recommendationType(request.getScenario())
                .build();

        RecommendationEntity entity = RecommendationEntity.builder()
                .recommendationId(UUID.randomUUID())
                .customerId(customerId)
                .conversationId(conversationId)
                .orderDraftId(orderDraftId)
                .recommendedItem("Bread")
                .reason(RecommendationReason.CONTEXTUAL_RELEVANCE)
                .recommendationType(RecommendationType.COMPLEMENTARY_PRODUCT)
                .score(0.5)
                .accepted(false)
                .dismissed(false)
                .build();

        RecommendationResponse response = RecommendationResponse.builder()
                .recommendationId(entity.getRecommendationId())
                .customerId(customerId)
                .conversationId(conversationId)
                .orderDraftId(orderDraftId)
                .recommendedItem(entity.getRecommendedItem())
                .reason(entity.getReason())
                .recommendationType(entity.getRecommendationType())
                .score(entity.getScore())
                .build();

        setAuthenticatedUser(user.getEmail());
        when(userRepository.findByEmail(user.getEmail())).thenReturn(Optional.of(user));
        when(memoryService.findRelevantMemory(any(UUID.class), any(OrderDraft.class))).thenReturn(List.of());
        when(contextBuilder.buildContext(any(), any(), any(), any(), any())).thenReturn(input);
        when(recommendationGenerator.generateRecommendations(input)).thenReturn(List.of(entity));
        when(recommendationRepository.saveAll(List.of(entity))).thenReturn(List.of(entity));
        when(recommendationMapper.toDto(entity)).thenReturn(response);

        RecommendationResponse actual = recommendationService.generateRecommendations(request, new ConversationResponse(), List.of(), new OrderDraft());

        assertNotNull(actual);
        assertEquals(entity.getRecommendedItem(), actual.getRecommendedItem());
        assertEquals(0.5, actual.getScore());
    }

    private void setAuthenticatedUser(String email) {
        UsernamePasswordAuthenticationToken auth = new UsernamePasswordAuthenticationToken(email, null, List.of());
        SecurityContext context = SecurityContextHolder.createEmptyContext();
        context.setAuthentication(auth);
        SecurityContextHolder.setContext(context);
    }
}
