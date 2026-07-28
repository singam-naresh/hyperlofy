package com.hyperlofy.backend.ai.platform.controller;

import com.hyperlofy.backend.ai.platform.entity.AiInferenceLog;
import com.hyperlofy.backend.ai.platform.entity.AiRecommendation;
import com.hyperlofy.backend.ai.platform.service.AiPlatformService;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.tags.Tag;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.*;

import java.math.BigDecimal;
import java.util.UUID;

@RestController
@RequestMapping("/api/v1/ai/platform/internal")
@RequiredArgsConstructor
@Tag(name = "AI Platform Services Internal API", description = "Endpoints for Hyperlofy services to request real-time personalized recommendations and record model inference execution telemetry")
@PreAuthorize("hasAnyRole('ADMIN', 'SUPER_ADMIN') or hasAuthority('SCOPE_internal')")
public class AiPlatformInternalController {

    private final AiPlatformService platformService;

    @PostMapping("/recommend")
    @Operation(summary = "Generate AI Personalized Recommendation", description = "Returns product, merchant, upsell, or cross-sell recommendation for customer home feed.")
    public ResponseEntity<AiRecommendation> generateRecommendation(
            @RequestParam UUID userId,
            @RequestParam String type,
            @RequestParam UUID entityId,
            @RequestParam(required = false) BigDecimal confidence) {
        return ResponseEntity.ok(platformService.generateRecommendation(userId, type, entityId, confidence));
    }

    @PostMapping("/inference/log")
    @Operation(summary = "Record AI Model Inference Execution Log", description = "Logs LLM model routing, latency, token consumption, and prompt key execution statistics.")
    public ResponseEntity<AiInferenceLog> recordInference(
            @RequestParam String modelName,
            @RequestParam(required = false) String promptKey,
            @RequestParam(required = false) UUID userId,
            @RequestParam(required = false) Integer tokenCount,
            @RequestParam(required = false) Integer executionMs) {
        return ResponseEntity.ok(platformService.recordInference(modelName, promptKey, userId, tokenCount, executionMs));
    }
}
