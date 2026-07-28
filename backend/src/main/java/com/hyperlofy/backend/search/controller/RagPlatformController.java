package com.hyperlofy.backend.search.controller;

import com.hyperlofy.backend.search.entity.RagChunk;
import com.hyperlofy.backend.search.service.SearchEnterpriseService;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.tags.Tag;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/api/v1/rag")
@RequiredArgsConstructor
@Tag(name = "Advanced RAG Retrieval Platform API", description = "Chunking, hybrid retrieval, Reciprocal Rank Fusion (RRF), re-ranking, and citation generation for RAG pipelines")
@PreAuthorize("hasAnyRole('ADMIN', 'SUPER_ADMIN') or hasAuthority('SCOPE_internal')")
public class RagPlatformController {

    private final SearchEnterpriseService enterpriseService;

    @PostMapping("/index")
    @Operation(summary = "Index Document RAG Chunk", description = "Indexes text chunk and embedding vector for Advanced RAG pipeline retrieval.")
    public ResponseEntity<RagChunk> indexChunk(
            @RequestParam String sourceId,
            @RequestParam String sourceType,
            @RequestParam(required = false) Integer chunkIndex,
            @RequestParam String chunkText,
            @RequestParam(required = false) String vectorEmbedding) {
        return ResponseEntity.ok(enterpriseService.indexRagChunk(sourceId, sourceType, chunkIndex, chunkText, vectorEmbedding));
    }
}
