package com.hyperlofy.backend.ai.merchantselection;

import java.util.function.Predicate;

public class MerchantEligibilitySpecification {

    public boolean isEligible(MerchantCandidate candidate) {
        return candidate != null && candidate.isActive() && candidate.isAvailable() && candidate.isCoveredByZone();
    }

    public Predicate<MerchantCandidate> asPredicate() {
        return this::isEligible;
    }
}
