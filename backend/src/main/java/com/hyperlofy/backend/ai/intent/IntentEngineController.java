package com.hyperlofy.backend.ai.intent;

import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequestMapping("/api/v1/intent")
@RequiredArgsConstructor
public class IntentEngineController {

    private final IntentEngineService intentEngineService;

    @PostMapping("/classify")
    public ResponseEntity<IntentResponse> classify(@Valid @RequestBody IntentRequest request) {
        return ResponseEntity.ok(intentEngineService.classify(request));
    }
}
