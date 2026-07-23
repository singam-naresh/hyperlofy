package com.hyperlofy.backend.ai.verify.dto;

import com.hyperlofy.backend.ai.verify.VerificationResult;
import com.hyperlofy.backend.ai.verify.VerificationType;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.time.OffsetDateTime;
import java.util.UUID;

@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class VerifyResponse {

    private UUID verificationId;
    private UUID orderId;
    private VerificationType verificationType;
    private VerificationResult verificationResult;
    private double score;
    private String message;
    private String details;
    private OffsetDateTime processedAt;
}
