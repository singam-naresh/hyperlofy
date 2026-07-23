package com.hyperlofy.backend.ai.learning;

import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Component;

@Component
@RequiredArgsConstructor
public class LearningProcessor {

    private final LearningService learningService;

    public LearningResponse processEvent(LearningRequest request) {
        return learningService.recordEvent(request);
    }
}
