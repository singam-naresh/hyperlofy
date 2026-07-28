package com.hyperlofy.backend.search.controller;

import com.hyperlofy.backend.search.entity.SearchConversation;
import com.hyperlofy.backend.search.service.SearchEnterpriseService;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.tags.Tag;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.*;

import java.util.UUID;

@RestController
@RequestMapping("/api/v1/ai-search")
@RequiredArgsConstructor
@Tag(name = "Enterprise AI Search & Conversational Discovery API", description = "Multi-turn conversational AI search, natural language question answering, intent detection, and citation source attribution")
@PreAuthorize("hasAnyRole('ADMIN', 'SUPER_ADMIN') or hasAuthority('SCOPE_internal')")
public class AiSearchController {

    private final SearchEnterpriseService enterpriseService;

    @PostMapping("/chat")
    @Operation(summary = "Execute Multi-turn Conversational AI Search", description = "Executes multi-turn AI search assistant conversation with context memory and cited source attribution.")
    public ResponseEntity<SearchConversation> chat(
            @RequestParam String conversationCode,
            @RequestParam UUID userId,
            @RequestParam String userQuery,
            @RequestParam(required = false) String tenantId) {
        return ResponseEntity.ok(enterpriseService.executeAiChat(conversationCode, userId, userQuery, tenantId));
    }
}
