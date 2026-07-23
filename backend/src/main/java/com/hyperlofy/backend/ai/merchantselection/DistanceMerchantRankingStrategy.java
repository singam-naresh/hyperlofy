package com.hyperlofy.backend.ai.merchantselection;

import org.springframework.stereotype.Component;

import java.util.Comparator;
import java.util.List;

@Component
public class DistanceMerchantRankingStrategy implements MerchantRankingStrategy {

    @Override
    public boolean supports(String intent) {
        return intent != null && (intent.equals("GROCERY") || intent.equals("MEDICINE") || intent.equals("ELECTRONICS") || intent.equals("FOOD") || intent.equals("CAKE") || intent.equals("FLOWERS") || intent.equals("PET_SUPPLIES") || intent.equals("DOCUMENT_DELIVERY") || intent.equals("PARCEL_DELIVERY") || intent.equals("ITEM_DELIVERY") || intent.equals("HELPER_REQUEST"));
    }

    @Override
    public List<MerchantCandidate> rank(List<MerchantCandidate> candidates, MerchantSelectionRequest request) {
        return candidates.stream()
                .sorted(Comparator.comparingDouble(MerchantCandidate::getScore).reversed()
                        .thenComparingDouble(MerchantCandidate::getDistanceKm))
                .toList();
    }
}
