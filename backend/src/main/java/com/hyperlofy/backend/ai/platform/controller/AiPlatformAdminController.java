package com.hyperlofy.backend.ai.platform.controller;

import com.hyperlofy.backend.ai.platform.entity.AiModelRegistry;
import com.hyperlofy.backend.ai.platform.entity.AiPrompt;
import com.hyperlofy.backend.ai.platform.service.AiPlatformService;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.tags.Tag;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/api/v1/ai/platform/admin")
@RequiredArgsConstructor
@Tag(name = "AI Platform Services Admin API", description = "Endpoints for AI Platform Engineers to manage model routing, prompt templates, and provider failover settings")
@PreAuthorize("hasAnyRole('ADMIN', 'SUPER_ADMIN')")
public class AiPlatformAdminController {

    private final AiPlatformService platformService;

    @PostMapping("/prompts")
    @Operation(summary = "Register or Update AI Prompt Template", description = "Creates or updates prompt template text with automatic versioning.")
    public ResponseEntity<AiPrompt> registerPrompt(
            @RequestParam String promptKey,
            @RequestParam String promptName,
            @RequestBody String templateText) {
        return ResponseEntity.ok(platformService.registerPromptTemplate(promptKey, promptName, templateText));
    }

    @GetMapping("/models")
    @Operation(summary = "Get Active LLM Model Registry", description = "Returns primary and fallback model routing registry (Gemini, OpenAI, Anthropic).")
    public ResponseEntity<List<AiModelRegistry>> getModels() {
        return ResponseEntity.ok(platformService.getRegisteredModels());
    }
}
