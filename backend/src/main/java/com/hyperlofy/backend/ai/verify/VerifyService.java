package com.hyperlofy.backend.ai.verify;

import com.hyperlofy.backend.ai.verify.dto.VerifyRequest;
import com.hyperlofy.backend.ai.verify.dto.VerifyResponse;
import com.hyperlofy.backend.common.exception.BusinessException;
import com.hyperlofy.backend.ai.verify.repository.VerifyRepository;
import com.hyperlofy.backend.order.entity.Order;
import com.hyperlofy.backend.order.repository.OrderRepository;
import com.hyperlofy.backend.user.entity.Role;
import com.hyperlofy.backend.user.entity.User;
import com.hyperlofy.backend.user.repository.UserRepository;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.HttpStatus;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.OffsetDateTime;
import java.util.List;
import java.util.UUID;

@Slf4j
@Service
@RequiredArgsConstructor
public class VerifyService {

    private final VerifyRepository verifyRepository;
    private final VerifyMapper verifyMapper;
    private final OrderRepository orderRepository;
    private final UserRepository userRepository;
    private final VerificationScoringService scoringService;

    @Transactional
    public VerifyResponse submitVerification(VerifyRequest request) {
        if (request == null) {
            throw new BusinessException("Verification request cannot be null", HttpStatus.BAD_REQUEST);
        }

        Order order = orderRepository.findById(request.getOrderId())
                .orElseThrow(() -> new BusinessException("Order not found", HttpStatus.NOT_FOUND));

        String email = org.springframework.security.core.context.SecurityContextHolder.getContext().getAuthentication().getName();
        User user = userRepository.findByEmail(email)
                .orElseThrow(() -> new BusinessException("Authenticated user not found", HttpStatus.UNAUTHORIZED));

        double score = scoringService.score(request.getVerificationType(), request.getPayload(), request.getExpectedValue(), request.getExpectedPrice());
        VerificationResult result = scoringService.evaluateResult(score, request.getVerificationType());

        VerifyEntity entity = VerifyEntity.builder()
                .verificationId(UUID.randomUUID())
                .order(order)
                .createdByUser(user)
                .verificationType(request.getVerificationType())
                .verificationResult(result)
                .payload(request.getPayload())
                .expectedValue(request.getExpectedValue())
                .expectedPrice(request.getExpectedPrice())
                .sourceUrl(request.getSourceUrl())
                .score(score)
                .message(result == VerificationResult.PASSED ? "Verification passed" : "Verification requires review")
                .details("Processed by Hyper Verify engine")
                .processedAt(OffsetDateTime.now())
                .active(true)
                .build();

        verifyRepository.save(entity);
        log.info("Verification result recorded for order {} type {} result {}", order.getId(), request.getVerificationType(), result);

        return verifyMapper.toDto(entity);
    }

    @Transactional(readOnly = true)
    public List<VerifyResponse> getVerificationsForOrder(UUID orderId) {
        Order order = orderRepository.findById(orderId)
                .orElseThrow(() -> new BusinessException("Order not found", HttpStatus.NOT_FOUND));
        verifyOrderAccess(order);
        return verifyRepository.findByOrder_IdAndActiveTrue(orderId).stream()
                .map(verifyMapper::toDto)
                .toList();
    }

    private void verifyOrderAccess(Order order) {
        String email = org.springframework.security.core.context.SecurityContextHolder.getContext().getAuthentication().getName();
        User user = userRepository.findByEmail(email)
                .orElseThrow(() -> new BusinessException("Authenticated user not found", HttpStatus.UNAUTHORIZED));

        if (user.getRole() == Role.ADMIN || user.getRole() == Role.SUPER_ADMIN) {
            return;
        }

        if (user.getRole() == Role.CUSTOMER && order.getCustomer() != null && order.getCustomer().getId().equals(user.getId())) {
            return;
        }

        if (user.getRole() == Role.AGENT && order.getAgent() != null && order.getAgent().getId().equals(user.getId())) {
            return;
        }

        throw new BusinessException("Access Denied: You do not have access to this order verification", HttpStatus.FORBIDDEN);
    }
}
