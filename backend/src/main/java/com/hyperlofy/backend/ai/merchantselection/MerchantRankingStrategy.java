package com.hyperlofy.backend.ai.merchantselection;

import java.util.List;

public interface MerchantRankingStrategy {
    boolean supports(String intent);
    List<MerchantCandidate> rank(List<MerchantCandidate> candidates, MerchantSelectionRequest request);
}
