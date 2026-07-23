package com.hyperlofy.backend.ai.merchantselection;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class MerchantScore {
    private double distanceWeight;
    private double availabilityWeight;
    private double fulfillmentWeight;
    private double ratingWeight;
    private double hoursWeight;
    private double zoneWeight;
    private double helperTravelWeight;
    private double reliabilityWeight;
    private double cancellationWeight;
    private double capabilityWeight;
    private double inventoryWeight;
    private double totalScore;
}
