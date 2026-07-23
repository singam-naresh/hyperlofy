package com.hyperlofy.backend.ai.controller;

import com.hyperlofy.backend.ai.dto.AiRequestDto;
import com.hyperlofy.backend.ai.dto.AiResponseDto;
import com.hyperlofy.backend.ai.gateway.AiGatewayService;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequestMapping("/api/v1/ai")
@RequiredArgsConstructor
public class AiGatewayController {

    private final AiGatewayService aiGatewayService;

    @PostMapping("/prompt")
    public ResponseEntity<AiResponseDto> prompt(@Valid @RequestBody AiRequestDto request) {
        return ResponseEntity.ok(aiGatewayService.prompt(request));
    }

    @GetMapping("/health")
    public ResponseEntity<String> health() {
        return ResponseEntity.ok("AI Gateway is online");
    }
}
