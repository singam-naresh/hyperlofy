package com.hyperlofy.backend.ai.verify;

import com.hyperlofy.backend.ai.verify.repository.VerifyRepository;
import com.hyperlofy.backend.ai.verify.dto.VerifyRequest;
import com.hyperlofy.backend.ai.verify.dto.VerifyResponse;
import com.hyperlofy.backend.common.exception.BusinessException;
import com.hyperlofy.backend.order.entity.Order;
import com.hyperlofy.backend.order.repository.OrderRepository;
import com.hyperlofy.backend.user.entity.Role;
import com.hyperlofy.backend.user.entity.User;
import com.hyperlofy.backend.user.repository.UserRepository;
import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.mockito.Mockito;
import org.springframework.http.HttpStatus;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContext;
import org.springframework.security.core.context.SecurityContextHolder;

import java.util.Optional;
import java.util.UUID;

import static org.junit.jupiter.api.Assertions.*;

class VerifyServiceTest {

    private VerifyRepository verifyRepository;
    private VerifyMapper verifyMapper;
    private OrderRepository orderRepository;
    private UserRepository userRepository;
    private VerificationScoringService scoringService;
    private VerifyService verifyService;

    @BeforeEach
    void setup() {
        verifyRepository = Mockito.mock(VerifyRepository.class);
        verifyMapper = new VerifyMapper();
        orderRepository = Mockito.mock(OrderRepository.class);
        userRepository = Mockito.mock(UserRepository.class);
        scoringService = new VerificationScoringService();
        verifyService = new VerifyService(verifyRepository, verifyMapper, orderRepository, userRepository, scoringService);
    }

    @AfterEach
    void cleanupSecurityContext() {
        SecurityContextHolder.clearContext();
    }

    @Test
    void submitVerificationThrowsWhenOrderMissing() {
        UUID orderId = UUID.randomUUID();
        VerifyRequest request = VerifyRequest.builder()
                .orderId(orderId)
                .verificationType(VerificationType.OCR)
                .payload("sample text")
                .build();

        Mockito.when(orderRepository.findById(orderId)).thenReturn(Optional.empty());

        BusinessException ex = assertThrows(BusinessException.class, () -> verifyService.submitVerification(request));
        assertEquals(HttpStatus.NOT_FOUND, ex.getStatus());
    }

    @Test
    void submitVerificationScoresAndRecords() {
        UUID orderId = UUID.randomUUID();
        UUID userId = UUID.randomUUID();
        Order order = new Order();
        order.setId(orderId);
        User user = new User();
        user.setId(userId);
        user.setEmail("test@hyperlofy.com");
        user.setRole(Role.CUSTOMER);

        Authentication authentication = new UsernamePasswordAuthenticationToken("test@hyperlofy.com", "password");
        SecurityContext context = SecurityContextHolder.createEmptyContext();
        context.setAuthentication(authentication);
        SecurityContextHolder.setContext(context);

        VerifyRequest request = VerifyRequest.builder()
                .orderId(orderId)
                .verificationType(VerificationType.OCR)
                .payload("The bill total is 250.00 and matches expected price 250.00")
                .expectedPrice(250.00)
                .build();

        Mockito.when(orderRepository.findById(orderId)).thenReturn(Optional.of(order));
        Mockito.when(userRepository.findByEmail(Mockito.anyString())).thenReturn(Optional.of(user));
        Mockito.when(verifyRepository.save(Mockito.any())).thenAnswer(invocation -> invocation.getArgument(0));

        VerifyResponse response = verifyService.submitVerification(request);

        assertNotNull(response);
        assertEquals(VerificationType.OCR, response.getVerificationType());
        assertTrue(response.getScore() > 0.0);
        assertNotNull(response.getVerificationId());
    }
}
