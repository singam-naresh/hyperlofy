package com.hyperlofy.backend.ai.merchantselection;

import com.hyperlofy.backend.ai.orderbuilder.OrderDraft;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.util.Map;
import java.util.UUID;

@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class MerchantSelectionRequest {
    private UUID requestId;
    private OrderDraft draft;
    private Double latitude;
    private Double longitude;
    private Map<String, Object> customerPreferences;
    private Map<String, Object> selectionConstraints;
}
