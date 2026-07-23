package com.hyperlofy.backend.ai.recommendation;

import com.hyperlofy.backend.ai.conversation.ConversationResponse;
import com.hyperlofy.backend.ai.memory.MemoryService;
import com.hyperlofy.backend.ai.memory.dto.MemoryDto;
import com.hyperlofy.backend.ai.merchantselection.MerchantCandidate;
import com.hyperlofy.backend.ai.orderbuilder.OrderDraft;
import com.hyperlofy.backend.ai.recommendation.dto.RecommendationActionRequest;
import com.hyperlofy.backend.ai.recommendation.dto.RecommendationRequest;
import com.hyperlofy.backend.ai.recommendation.dto.RecommendationResponse;
import com.hyperlofy.backend.ai.recommendation.repository.RecommendationRepository;
import com.hyperlofy.backend.common.exception.BusinessException;
import com.hyperlofy.backend.order.entity.Order;
import com.hyperlofy.backend.order.repository.OrderRepository;
import com.hyperlofy.backend.user.entity.Role;
import com.hyperlofy.backend.user.entity.User;
import com.hyperlofy.backend.user.repository.UserRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.OffsetDateTime;
import java.util.List;
import java.util.UUID;
import java.util.stream.Collectors;

@Service
@RequiredArgsConstructor
public class RecommendationService {

    private final RecommendationRepository recommendationRepository;
    private final RecommendationMapper recommendationMapper;
    private final RecommendationGenerator recommendationGenerator;
    private final RecommendationRanker recommendationRanker;
    private final RecommendationContextBuilder contextBuilder;
    private final MemoryService memoryService;
    private final OrderRepository orderRepository;
    private final UserRepository userRepository;

    @Transactional(readOnly = true)
    public List<RecommendationResponse> fetchRecommendations(UUID customerId) {
        User user = getCurrentAuthenticatedUser();
        if (!isAuthorized(user, customerId)) {
            throw new BusinessException("Access denied", HttpStatus.FORBIDDEN);
        }

        return recommendationRepository.findByCustomerIdAndDismissedFalse(customerId).stream()
                .map(recommendationMapper::toDto)
                .collect(Collectors.toList());
    }

    @Transactional
    public RecommendationResponse generateRecommendations(RecommendationRequest request,
                                                          ConversationResponse conversation,
                                                          List<MerchantCandidate> merchantCandidates,
                                                          OrderDraft draft) {
        validateRequest(request);
        User user = getCurrentAuthenticatedUser();
        if (!isAuthorized(user, request.getCustomerId())) {
            throw new BusinessException("Access denied", HttpStatus.FORBIDDEN);
        }

        List<MemoryDto> memories = memoryService.findRelevantMemory(request.getCustomerId(), draft);
        RecommendationInput context = contextBuilder.buildContext(request, conversation, memories, merchantCandidates, draft);
        List<RecommendationEntity> entities = recommendationGenerator.generateRecommendations(context);
        List<RecommendationEntity> saved = recommendationRepository.saveAll(entities);
        return recommendationMapper.toDto(saved.get(0));
    }

    @Transactional
    public RecommendationResponse acceptRecommendation(RecommendationActionRequest request) {
        RecommendationEntity entity = recommendationRepository.findById(request.getRecommendationId())
                .orElseThrow(() -> new BusinessException("Recommendation not found", HttpStatus.NOT_FOUND));

        User user = getCurrentAuthenticatedUser();
        if (!isAuthorized(user, entity.getCustomerId())) {
            throw new BusinessException("Access denied", HttpStatus.FORBIDDEN);
        }

        entity.setAccepted(true);
        entity.setDismissed(false);
        recommendationRepository.save(entity);
        return recommendationMapper.toDto(entity);
    }

    @Transactional
    public RecommendationResponse dismissRecommendation(RecommendationActionRequest request) {
        RecommendationEntity entity = recommendationRepository.findById(request.getRecommendationId())
                .orElseThrow(() -> new BusinessException("Recommendation not found", HttpStatus.NOT_FOUND));

        User user = getCurrentAuthenticatedUser();
        if (!isAuthorized(user, entity.getCustomerId())) {
            throw new BusinessException("Access denied", HttpStatus.FORBIDDEN);
        }

        entity.setDismissed(true);
        entity.setAccepted(false);
        recommendationRepository.save(entity);
        return recommendationMapper.toDto(entity);
    }

    private void validateRequest(RecommendationRequest request) {
        if (request == null) {
            throw new BusinessException("Recommendation request cannot be null", HttpStatus.BAD_REQUEST);
        }
        if (request.getCustomerId() == null || request.getConversationId() == null || request.getOrderDraftId() == null) {
            throw new BusinessException("Recommendation request is missing required context", HttpStatus.BAD_REQUEST);
        }
    }

    private boolean isAuthorized(User user, UUID customerId) {
        return user != null && (user.getRole() == Role.ADMIN || user.getRole() == Role.SUPER_ADMIN || user.getId().equals(customerId));
    }

    private User getCurrentAuthenticatedUser() {
        String email = org.springframework.security.core.context.SecurityContextHolder.getContext().getAuthentication().getName();
        return userRepository.findByEmail(email)
                .orElseThrow(() -> new BusinessException("Authenticated user not found", HttpStatus.UNAUTHORIZED));
    }
}
