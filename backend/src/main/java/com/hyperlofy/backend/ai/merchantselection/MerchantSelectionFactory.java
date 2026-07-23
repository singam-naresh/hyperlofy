package com.hyperlofy.backend.ai.merchantselection;

import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Component;

import java.util.List;

@Component
@RequiredArgsConstructor
public class MerchantSelectionFactory {

    private final List<MerchantRankingStrategy> rankingStrategies;

    public MerchantRankingStrategy resolve(String intent) {
        return rankingStrategies.stream()
                .filter(strategy -> strategy.supports(intent))
                .findFirst()
                .orElseThrow(() -> new IllegalArgumentException("No ranking strategy registered for intent=" + intent));
    }
}
