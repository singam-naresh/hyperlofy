package com.hyperlofy.backend.ai.intent;

import org.springframework.stereotype.Component;

@Component
public class IntentResponseValidator {

    public boolean isValid(IntentResponse response) {
        if (response == null) {
            return false;
        }
        if (response.getIntent() == null || response.getPlan() == null) {
            return false;
        }
        if (response.getMessage() == null || response.getMessage().isBlank()) {
            return false;
        }
        if (response.getConfidence() < 0.0 || response.getConfidence() > 1.0) {
            return false;
        }
        return true;
    }
}
