package com.hyperlofy.backend.pricing.service;

import com.hyperlofy.backend.pricing.entity.PricingQuote;
import com.hyperlofy.backend.pricing.entity.PricingQuoteVersion;
import com.hyperlofy.backend.pricing.entity.PricingRule;
import com.hyperlofy.backend.pricing.repository.PricingQuoteRepository;
import com.hyperlofy.backend.pricing.repository.PricingQuoteVersionRepository;
import com.hyperlofy.backend.pricing.repository.PricingRuleRepository;
import lombok.RequiredArgsConstructor;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.math.BigDecimal;
import java.math.RoundingMode;
import java.time.ZonedDateTime;
import java.util.UUID;

@Service
@RequiredArgsConstructor
public class DynamicPricingService {

    private static final Logger log = LoggerFactory.getLogger(DynamicPricingService.class);

    private final PricingQuoteRepository quoteRepository;
    private final PricingQuoteVersionRepository quoteVersionRepository;
    private final PricingRuleRepository ruleRepository;

    @Transactional
    public PricingQuote calculateQuote(UUID orderId, String serviceType, String serviceLevel, Double distanceKm, Integer estMinutes, Double surgeMultiplier) {
        log.info("[DYNAMIC PRICING ENGINE] Calculating quote Service={}, Level={}, Dist={}km, Duration={}min, Surge={}x",
                serviceType, serviceLevel, distanceKm, estMinutes, surgeMultiplier);

        PricingRule rule = ruleRepository.findByServiceTypeAndIsActiveTrue(serviceType).orElseGet(() ->
                PricingRule.builder()
                        .ruleName("DEFAULT_" + serviceType)
                        .serviceType(serviceType)
                        .baseFare(new BigDecimal("40.00"))
                        .minFare(new BigDecimal("50.00"))
                        .perKmRate(new BigDecimal("12.00"))
                        .perMinuteRate(new BigDecimal("2.00"))
                        .build()
        );

        BigDecimal distCharge = rule.getPerKmRate().multiply(BigDecimal.valueOf(distanceKm));
        BigDecimal timeCharge = rule.getPerMinuteRate().multiply(BigDecimal.valueOf(estMinutes));

        BigDecimal rawTotal = rule.getBaseFare().add(distCharge).add(timeCharge);
        rawTotal = rawTotal.max(rule.getMinFare());

        double surge = surgeMultiplier != null ? surgeMultiplier : 1.0;
        BigDecimal surgedTotal = rawTotal.multiply(BigDecimal.valueOf(surge));

        BigDecimal platformFee = new BigDecimal("10.00");
        BigDecimal serviceFee = new BigDecimal("5.00");
        BigDecimal subtotal = surgedTotal.add(platformFee).add(serviceFee);
        BigDecimal tax = subtotal.multiply(new BigDecimal("0.18")).setScale(2, RoundingMode.HALF_UP);
        BigDecimal finalTotal = subtotal.add(tax).setScale(2, RoundingMode.HALF_UP);

        PricingQuote quote = PricingQuote.builder()
                .orderId(orderId)
                .serviceType(serviceType)
                .serviceLevel(serviceLevel != null ? serviceLevel : "STANDARD")
                .baseFare(rule.getBaseFare())
                .distanceCharge(distCharge.setScale(2, RoundingMode.HALF_UP))
                .timeCharge(timeCharge.setScale(2, RoundingMode.HALF_UP))
                .surgeMultiplier(surge)
                .serviceFee(serviceFee)
                .platformFee(platformFee)
                .taxAmount(tax)
                .totalAmount(finalTotal)
                .status("QUOTE_CREATED")
                .expiresAt(ZonedDateTime.now().plusMinutes(15))
                .build();

        PricingQuote saved = quoteRepository.save(quote);

        PricingQuoteVersion version = PricingQuoteVersion.builder()
                .quoteId(saved.getId())
                .versionNumber(1)
                .totalAmount(finalTotal)
                .recalculationReason("INITIAL_QUOTE_GENERATION")
                .build();

        quoteVersionRepository.save(version);
        return saved;
    }

    @Transactional
    public PricingQuote recalculateQuote(UUID quoteId, Double newDistanceKm, Integer newDurationMinutes, String reason) {
        log.info("[DYNAMIC PRICING ENGINE] Recalculating quote QuoteId={}, NewDist={}km, NewDuration={}min, Reason={}",
                quoteId, newDistanceKm, newDurationMinutes, reason);

        PricingQuote quote = quoteRepository.findById(quoteId)
                .orElseThrow(() -> new IllegalArgumentException("PricingQuote not found: " + quoteId));

        BigDecimal newDistCharge = new BigDecimal("12.00").multiply(BigDecimal.valueOf(newDistanceKm));
        BigDecimal newTimeCharge = new BigDecimal("2.00").multiply(BigDecimal.valueOf(newDurationMinutes));
        BigDecimal rawTotal = quote.getBaseFare().add(newDistCharge).add(newTimeCharge);
        BigDecimal surgedTotal = rawTotal.multiply(BigDecimal.valueOf(quote.getSurgeMultiplier()));
        BigDecimal subtotal = surgedTotal.add(quote.getPlatformFee()).add(quote.getServiceFee());
        BigDecimal tax = subtotal.multiply(new BigDecimal("0.18")).setScale(2, RoundingMode.HALF_UP);
        BigDecimal finalTotal = subtotal.add(tax).setScale(2, RoundingMode.HALF_UP);

        quote.setDistanceCharge(newDistCharge.setScale(2, RoundingMode.HALF_UP));
        quote.setTimeCharge(newTimeCharge.setScale(2, RoundingMode.HALF_UP));
        quote.setTaxAmount(tax);
        quote.setTotalAmount(finalTotal);
        quote.setStatus("PRICE_RECALCULATED");

        PricingQuote saved = quoteRepository.save(quote);
        int nextVersion = quoteVersionRepository.findByQuoteIdOrderByVersionNumberAsc(quoteId).size() + 1;

        PricingQuoteVersion version = PricingQuoteVersion.builder()
                .quoteId(quoteId)
                .versionNumber(nextVersion)
                .totalAmount(finalTotal)
                .recalculationReason(reason)
                .build();

        quoteVersionRepository.save(version);
        return saved;
    }

    @Transactional(readOnly = true)
    public PricingQuote getQuote(UUID quoteId) {
        return quoteRepository.findById(quoteId)
                .orElseThrow(() -> new IllegalArgumentException("PricingQuote not found: " + quoteId));
    }
}
