package com.hyperlofy.backend.ai.learning;

import com.hyperlofy.backend.common.exception.BusinessException;
import com.hyperlofy.backend.user.entity.Role;
import com.hyperlofy.backend.user.entity.User;
import com.hyperlofy.backend.user.repository.UserRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.OffsetDateTime;
import java.util.Comparator;
import java.util.List;
import java.util.UUID;
import java.util.stream.Collectors;

@Service
@RequiredArgsConstructor
public class LearningService {

    private final LearningRepository learningRepository;
    private final LearningMapper learningMapper;
    private final UserRepository userRepository;
    private final LearningRuleEngine learningRuleEngine;
    private final LearningStatisticsService statisticsService;

    @Transactional
    public LearningResponse recordEvent(LearningRequest request) {
        validateRequest(request);
        User customer = findCustomer(request.getCustomerId());

        LearningScore score = learningRuleEngine.evaluate(request);
        LearningEntity entity = LearningEntity.builder()
                .learningId(UUID.randomUUID())
                .customer(customer)
                .conversationId(request.getConversationId())
                .orderId(request.getOrderId())
                .merchantId(request.getMerchantId())
                .recommendationId(request.getRecommendationId())
                .learningType(request.getLearningType())
                .score(score.getWeightedScore())
                .confidence(score.getConfidence())
                .recency(score.getRecency())
                .frequency(score.getFrequency())
                .details(request.getDetails())
                .eventAt(OffsetDateTime.now())
                .build();

        return learningMapper.toDto(learningRepository.save(entity));
    }

    @Transactional(readOnly = true)
    public List<LearningResponse> getCustomerLearning(UUID customerId) {
        User caller = getCurrentAuthenticatedUser();
        if (!isAuthorized(caller, customerId)) {
            throw new BusinessException("Access denied", HttpStatus.FORBIDDEN);
        }
        return learningRepository.findByCustomer_IdOrderByEventAtDesc(customerId).stream()
                .map(learningMapper::toDto)
                .collect(Collectors.toList());
    }

    @Transactional(readOnly = true)
    public List<LearningResponse> getMerchantLearning(UUID merchantId) {
        User caller = getCurrentAuthenticatedUser();
        if (caller.getRole() != Role.ADMIN && caller.getRole() != Role.SUPER_ADMIN) {
            throw new BusinessException("Access denied", HttpStatus.FORBIDDEN);
        }
        return learningRepository.findByMerchantIdOrderByEventAtDesc(merchantId).stream()
                .map(learningMapper::toDto)
                .collect(Collectors.toList());
    }

    @Transactional(readOnly = true)
    public LearningSummary getStatistics(UUID customerId) {
        return statisticsService.calculateSummary(customerId);
    }

    private void validateRequest(LearningRequest request) {
        if (request == null) {
            throw new BusinessException("Learning request cannot be null", HttpStatus.BAD_REQUEST);
        }
        if (request.getCustomerId() == null || request.getLearningType() == null) {
            throw new BusinessException("Learning request requires customerId and learningType", HttpStatus.BAD_REQUEST);
        }
    }

    private User findCustomer(UUID customerId) {
        return userRepository.findById(customerId)
                .orElseThrow(() -> new BusinessException("Customer not found", HttpStatus.NOT_FOUND));
    }

    private User getCurrentAuthenticatedUser() {
        String email = org.springframework.security.core.context.SecurityContextHolder.getContext().getAuthentication().getName();
        return userRepository.findByEmail(email)
                .orElseThrow(() -> new BusinessException("Authenticated user not found", HttpStatus.UNAUTHORIZED));
    }

    private boolean isAuthorized(User user, UUID customerId) {
        return user != null && (user.getRole() == Role.ADMIN || user.getRole() == Role.SUPER_ADMIN || user.getId().equals(customerId));
    }
}
