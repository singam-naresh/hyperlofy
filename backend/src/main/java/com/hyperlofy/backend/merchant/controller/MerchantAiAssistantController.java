package com.hyperlofy.backend.merchant.controller;

import com.hyperlofy.backend.ai.genai.service.AiGatewayService;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.tags.Tag;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.*;

import java.security.Principal;
import java.util.Map;
import java.util.UUID;

@RestController
@RequestMapping("/api/v1/merchant/ai")
@RequiredArgsConstructor
@Tag(name = "Merchant AI Assistant API", description = "Endpoints for AI marketing copy generation, sales trend explanations, and store optimization advice")
@PreAuthorize("hasAnyRole('MERCHANT', 'ADMIN', 'SUPER_ADMIN')")
public class MerchantAiAssistantController {

    private final AiGatewayService aiGatewayService;

    @PostMapping("/generate-copy")
    @Operation(summary = "Generate Marketing Copy", description = "Uses Generative AI to craft store marketing copy, product descriptions, and promotional slogans.")
    public ResponseEntity<Map<String, Object>> generateCopy(Principal principal, @RequestParam String topic) {
        return ResponseEntity.ok(aiGatewayService.generateMerchantCopy(UUID.randomUUID(), topic));
    }
}
