package com.hyperlofy.backend.ai.verify;

import com.hyperlofy.backend.ai.verify.dto.VerifyResponse;
import org.springframework.stereotype.Component;

@Component
public class VerifyMapper {

    public VerifyResponse toDto(VerifyEntity entity) {
        if (entity == null) {
            return null;
        }

        return VerifyResponse.builder()
                .verificationId(entity.getVerificationId())
                .orderId(entity.getOrder() != null ? entity.getOrder().getId() : null)
                .verificationType(entity.getVerificationType())
                .verificationResult(entity.getVerificationResult())
                .score(entity.getScore())
                .message(entity.getMessage())
                .details(entity.getDetails())
                .processedAt(entity.getProcessedAt())
                .build();
    }
}
