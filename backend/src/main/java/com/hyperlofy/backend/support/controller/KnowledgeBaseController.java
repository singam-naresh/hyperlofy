package com.hyperlofy.backend.support.controller;

import com.hyperlofy.backend.support.entity.KnowledgeArticle;
import com.hyperlofy.backend.support.service.CustomerSupportService;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.tags.Tag;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/api/v1/knowledge/articles")
@RequiredArgsConstructor
@Tag(name = "Support Knowledge Base & SOPs API", description = "Query customer FAQs, merchant SOPs, agent canned macros, and AI-powered help articles")
@PreAuthorize("hasAnyRole('USER', 'SUPPORT', 'AGENT', 'ADMIN', 'SUPER_ADMIN') or hasAuthority('SCOPE_internal')")
public class KnowledgeBaseController {

    private final CustomerSupportService supportService;

    @GetMapping
    @Operation(summary = "Get Knowledge Base Articles", description = "Returns customer FAQs and support SOPs filtered by category.")
    public ResponseEntity<List<KnowledgeArticle>> getArticles(@RequestParam(required = false) String category) {
        return ResponseEntity.ok(supportService.getKnowledgeArticles(category));
    }
}
