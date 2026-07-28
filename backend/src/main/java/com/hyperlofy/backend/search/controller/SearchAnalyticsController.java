package com.hyperlofy.backend.search.controller;

import com.hyperlofy.backend.search.entity.VectorEmbedding;
import com.hyperlofy.backend.search.service.EnterpriseSearchService;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.tags.Tag;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/api/v1/search/analytics")
@RequiredArgsConstructor
@Tag(name = "Search Analytics & RAG Foundation API", description = "Query search performance, zero-result queries, click-through rates, and generate chunk-level vector embeddings for RAG retrieval")
@PreAuthorize("hasAnyRole('ADMIN', 'SUPER_ADMIN') or hasAuthority('SCOPE_internal')")
public class SearchAnalyticsController {

    private final EnterpriseSearchService searchService;

    @PostMapping("/embedding")
    @Operation(summary = "Generate & Store Chunk Vector Embedding", description = "Stores chunk-level vector embeddings for RAG (Retrieval-Augmented Generation) knowledge grounding using Gemini text embeddings.")
    public ResponseEntity<VectorEmbedding> storeEmbedding(
            @RequestParam String sourceDocId,
            @RequestParam String sourceDocType,
            @RequestParam String chunkText,
            @RequestParam String embeddingVector) {
        return ResponseEntity.ok(searchService.generateVectorEmbedding(sourceDocId, sourceDocType, chunkText, embeddingVector));
    }
}
