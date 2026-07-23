package com.hyperlofy.backend.ai.verify;

import org.springframework.stereotype.Service;

@Service
public class VerificationScoringService {

    public double score(VerificationType type, String payload, String expectedValue, Double expectedPrice) {
        if (type == null || payload == null || payload.isBlank()) {
            return 0.0;
        }

        double baseConfidence = switch (type) {
            case IMAGE -> 0.70;
            case BARCODE -> 0.80;
            case OCR -> 0.75;
            case PRICE -> 0.65;
            case DOCUMENT -> 0.70;
            default -> 0.50;
        };

        double normalizedLength = Math.min(1.0, payload.length() / 1500.0);
        double valueMatch = expectedValue != null && !expectedValue.isBlank() && payload.toLowerCase().contains(expectedValue.toLowerCase()) ? 0.12 : 0.0;
        double priceMatch = expectedPrice != null && payload.contains(expectedPrice.toString()) ? 0.10 : 0.0;

        return Math.min(1.0, baseConfidence + normalizedLength * 0.15 + valueMatch + priceMatch);
    }

    public VerificationResult evaluateResult(double score, VerificationType type) {
        if (score >= 0.85) {
            return VerificationResult.PASSED;
        }
        if (score >= 0.55) {
            return VerificationResult.WARNING;
        }
        return VerificationResult.FAILED;
    }
}
