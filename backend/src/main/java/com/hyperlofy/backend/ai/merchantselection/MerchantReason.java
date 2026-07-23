package com.hyperlofy.backend.ai.merchantselection;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class MerchantReason {
    private String code;
    private String message;
}
