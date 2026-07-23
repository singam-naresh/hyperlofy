package com.hyperlofy.backend.ai.conversation;

import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.UUID;

@RestController
@RequestMapping("/api/v1/conversations")
@RequiredArgsConstructor
public class ConversationEngineController {

    private final ConversationService conversationService;

    @PostMapping("/start")
    public ResponseEntity<ConversationResponse> start(@Valid @RequestBody ConversationRequest request) {
        return ResponseEntity.ok(conversationService.process(request));
    }

    @PostMapping("/{conversationId}/resume")
    public ResponseEntity<ConversationResponse> resume(@PathVariable UUID conversationId, @Valid @RequestBody ConversationRequest request) {
        return ResponseEntity.ok(conversationService.resume(conversationId, request));
    }
}
