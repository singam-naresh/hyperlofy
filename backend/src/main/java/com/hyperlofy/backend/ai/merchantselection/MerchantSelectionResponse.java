package com.hyperlofy.backend.ai.merchantselection;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class MerchantSelectionResponse {
    private boolean success;
    private MerchantSelectionPlan plan;
    private String message;
}
