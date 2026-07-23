package com.hyperlofy.backend.ai.verify.dto;

import com.hyperlofy.backend.ai.verify.VerificationType;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.util.HashMap;
import java.util.Map;
import java.util.UUID;

@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class VerifyRequest {

    @NotNull
    private UUID orderId;

    @NotNull
    private VerificationType verificationType;

    @NotBlank
    private String payload;

    private String expectedValue;

    private Double expectedPrice;

    private String sourceUrl;

    @Builder.Default
    private Map<String, Object> metadata = new HashMap<>();
}
