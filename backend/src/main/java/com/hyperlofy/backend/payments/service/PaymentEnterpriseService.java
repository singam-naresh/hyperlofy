package com.hyperlofy.backend.payments.service;

import com.hyperlofy.backend.payments.entity.PaymentGatewayRouting;
import com.hyperlofy.backend.payments.entity.PaymentSubscription;
import com.hyperlofy.backend.payments.entity.PaymentToken;
import com.hyperlofy.backend.payments.repository.PaymentGatewayRoutingRepository;
import com.hyperlofy.backend.payments.repository.PaymentSubscriptionRepository;
import com.hyperlofy.backend.payments.repository.PaymentTokenRepository;
import lombok.RequiredArgsConstructor;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.math.BigDecimal;
import java.time.ZonedDateTime;
import java.util.List;
import java.util.UUID;

@Service
@RequiredArgsConstructor
public class PaymentEnterpriseService {

    private static final Logger log = LoggerFactory.getLogger(PaymentEnterpriseService.class);

    private final PaymentGatewayRoutingRepository routingRepository;
    private final PaymentTokenRepository tokenRepository;
    private final PaymentSubscriptionRepository subscriptionRepository;

    @Transactional(readOnly = true)
    public String selectOptimalGateway() {
        List<PaymentGatewayRouting> activeGateways = routingRepository.findByIsActiveTrueAndIsBlacklistedFalseOrderByPriorityOrderAsc();
        if (activeGateways.isEmpty()) {
            log.warn("[PAYMENTS ENTERPRISE] No active gateway routing configuration found. Defaulting to RAZORPAY");
            return "RAZORPAY";
        }

        PaymentGatewayRouting topChoice = activeGateways.get(0);
        log.info("[PAYMENTS ENTERPRISE] Selected optimal gateway={} (Priority={}, SuccessRate={}% Latency={}ms)",
                topChoice.getGatewayName(), topChoice.getPriorityOrder(), topChoice.getSuccessRatePercent(), topChoice.getAverageLatencyMs());
        return topChoice.getGatewayName();
    }

    @Transactional
    public PaymentToken tokenizeCardReference(UUID customerId, String providerName, String cardAlias, Integer expMonth, Integer expYear) {
        log.info("[PAYMENTS ENTERPRISE] PCI-Compliant tokenization for CustomerId={}, Provider={}, Alias={}", customerId, providerName, cardAlias);

        String tokenRef = "tok_" + UUID.randomUUID().toString().replace("-", "");
        PaymentToken token = PaymentToken.builder()
                .customerId(customerId)
                .providerName(providerName)
                .paymentTokenRef(tokenRef)
                .cardAlias(cardAlias)
                .expiryMonth(expMonth)
                .expiryYear(expYear)
                .isDefault(true)
                .build();

        return tokenRepository.save(token);
    }

    @Transactional
    public PaymentSubscription createSubscription(UUID customerId, String planName, BigDecimal amount) {
        log.info("[PAYMENTS ENTERPRISE] Creating recurring subscription Plan={}, Amount={} for CustomerId={}", planName, amount, customerId);

        PaymentSubscription sub = PaymentSubscription.builder()
                .customerId(customerId)
                .planName(planName)
                .billingAmount(amount)
                .billingInterval("MONTHLY")
                .status("ACTIVE")
                .nextBillingDate(ZonedDateTime.now().plusMonths(1))
                .build();

        return subscriptionRepository.save(sub);
    }

    @Transactional(readOnly = true)
    public List<PaymentToken> getCustomerTokens(UUID customerId) {
        return tokenRepository.findByCustomerId(customerId);
    }
}
