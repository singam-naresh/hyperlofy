package com.hyperlofy.backend.ai.merchantselection;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.util.List;
import java.util.UUID;

@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class MerchantCandidate {
    private UUID merchantId;
    private String merchantName;
    private String zoneName;
    private double latitude;
    private double longitude;
    private double distanceKm;
    private boolean available;
    private boolean active;
    private boolean coveredByZone;
    private double score;
    private List<String> capabilities;
    private String inventoryStatus;
}
