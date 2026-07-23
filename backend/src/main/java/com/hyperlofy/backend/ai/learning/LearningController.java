package com.hyperlofy.backend.ai.learning;

import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import java.util.List;
import java.util.UUID;

@RestController
@RequestMapping("/api/v1/learning")
@RequiredArgsConstructor
public class LearningController {

    private final LearningService learningService;
    private final CustomerPreferenceService customerPreferenceService;
    private final MerchantFeedbackService merchantFeedbackService;
    private final RecommendationFeedbackService recommendationFeedbackService;

    @PostMapping("/event")
    public ResponseEntity<LearningResponse> recordEvent(@Valid @RequestBody LearningRequest request) {
        return ResponseEntity.ok(learningService.recordEvent(request));
    }

    @GetMapping("/customer/{customerId}")
    public ResponseEntity<List<LearningResponse>> getCustomerLearning(@PathVariable UUID customerId) {
        return ResponseEntity.ok(learningService.getCustomerLearning(customerId));
    }

    @GetMapping("/merchant/{merchantId}")
    public ResponseEntity<List<MerchantScoreDto>> getMerchantLearning(@PathVariable UUID merchantId) {
        return ResponseEntity.ok(merchantFeedbackService.getMerchantFeedback(merchantId));
    }

    @GetMapping("/statistics/{customerId}")
    public ResponseEntity<LearningSummary> getStatistics(@PathVariable UUID customerId) {
        return ResponseEntity.ok(learningService.getStatistics(customerId));
    }

    @GetMapping("/preferences/{customerId}")
    public ResponseEntity<List<PreferenceScoreDto>> getPreferences(@PathVariable UUID customerId) {
        return ResponseEntity.ok(customerPreferenceService.getCustomerPreferences(customerId));
    }

    @GetMapping("/recommendations/{customerId}")
    public ResponseEntity<List<RecommendationScoreDto>> getRecommendationFeedback(@PathVariable UUID customerId) {
        return ResponseEntity.ok(recommendationFeedbackService.getRecommendationFeedback(customerId));
    }
}
