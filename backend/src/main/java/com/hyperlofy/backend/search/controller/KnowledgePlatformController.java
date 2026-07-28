package com.hyperlofy.backend.search.controller;

import com.hyperlofy.backend.search.entity.KnowledgeArticle;
import com.hyperlofy.backend.search.service.KnowledgePlatformService;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.tags.Tag;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.*;

import java.util.List;
import java.util.UUID;

@RestController
@RequestMapping("/api/v1/knowledge")
@RequiredArgsConstructor
@Tag(name = "Enterprise Knowledge Platform API", description = "Author, review, publish, and search enterprise SOPs, merchant guides, runbooks, compliance policies, and FAQs")
@PreAuthorize("hasAnyRole('ADMIN', 'SUPER_ADMIN') or hasAuthority('SCOPE_internal')")
public class KnowledgePlatformController {

    private final KnowledgePlatformService knowledgeService;

    @PostMapping
    @Operation(summary = "Create Knowledge Article Draft", description = "Authors a new knowledge article (SOP, GUIDE, POLICY, RUNBOOK, FAQ) in DRAFT state.")
    public ResponseEntity<KnowledgeArticle> createArticle(
            @RequestParam String articleKey,
            @RequestParam String title,
            @RequestParam String articleType,
            @RequestParam String content,
            @RequestParam(required = false) String summary,
            @RequestParam(required = false) UUID categoryId,
            @RequestParam UUID authorUserId,
            @RequestParam(required = false) String tags,
            @RequestParam(required = false) UUID tenantId) {
        return ResponseEntity.ok(knowledgeService.createArticle(articleKey, title, articleType, content, summary, categoryId, authorUserId, tags, tenantId));
    }

    @GetMapping
    @Operation(summary = "List All Knowledge Articles", description = "Returns all published and draft knowledge articles across SOPs, guides, policies, and FAQs.")
    public ResponseEntity<List<KnowledgeArticle>> listArticles() {
        return ResponseEntity.ok(knowledgeService.getAllArticles());
    }

    @GetMapping("/{id}")
    @Operation(summary = "Get Knowledge Article Details", description = "Returns full content, metadata, view count, and helpful votes for specified article ID.")
    public ResponseEntity<KnowledgeArticle> getArticle(@PathVariable UUID id) {
        return ResponseEntity.ok(knowledgeService.getArticle(id));
    }

    @PostMapping("/publish")
    @Operation(summary = "Publish Knowledge Article", description = "Approves and transitions knowledge article draft to PUBLISHED state with reviewer signature.")
    public ResponseEntity<KnowledgeArticle> publishArticle(
            @RequestParam UUID articleId,
            @RequestParam UUID reviewerUserId) {
        return ResponseEntity.ok(knowledgeService.publishArticle(articleId, reviewerUserId));
    }
}
