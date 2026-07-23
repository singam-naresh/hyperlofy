package com.hyperlofy.backend.ai.recommendation;

import com.hyperlofy.backend.ai.conversation.ConversationResponse;
import com.hyperlofy.backend.ai.recommendation.dto.RecommendationActionRequest;
import com.hyperlofy.backend.ai.recommendation.dto.RecommendationRequest;
import com.hyperlofy.backend.ai.recommendation.dto.RecommendationResponse;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;
import java.util.UUID;

@RestController
@RequestMapping("/api/v1/recommendations")
@RequiredArgsConstructor
public class RecommendationController {

    private final RecommendationService recommendationService;

    @GetMapping(produces = MediaType.APPLICATION_JSON_VALUE)
    public ResponseEntity<List<RecommendationResponse>> getRecommendations(@RequestParam UUID customerId) {
        return ResponseEntity.ok(recommendationService.fetchRecommendations(customerId));
    }

    @PostMapping(consumes = MediaType.APPLICATION_JSON_VALUE, produces = MediaType.APPLICATION_JSON_VALUE)
    public ResponseEntity<RecommendationResponse> generateRecommendations(
            @Valid @RequestBody RecommendationRequest request) {

        RecommendationResponse response = recommendationService.generateRecommendations(request, null, List.of(), null);
        return ResponseEntity.ok(response);
    }

    @PostMapping("/accept")
    public ResponseEntity<RecommendationResponse> acceptRecommendation(@Valid @RequestBody RecommendationActionRequest request) {
        return ResponseEntity.ok(recommendationService.acceptRecommendation(request));
    }

    @PostMapping("/dismiss")
    public ResponseEntity<RecommendationResponse> dismissRecommendation(@Valid @RequestBody RecommendationActionRequest request) {
        return ResponseEntity.ok(recommendationService.dismissRecommendation(request));
    }
}
