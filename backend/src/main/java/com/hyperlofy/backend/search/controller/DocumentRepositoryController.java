package com.hyperlofy.backend.search.controller;

import com.hyperlofy.backend.search.entity.SearchDocument;
import com.hyperlofy.backend.search.service.EnterpriseSearchService;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.tags.Tag;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.*;

import java.util.UUID;

@RestController
@RequestMapping("/api/v1/documents")
@RequiredArgsConstructor
@Tag(name = "Enterprise Document Repository API", description = "Index and retrieve enterprise documents (PDF, DOCX, XLSX, JSON, CSV, Markdown) with full-text & vector metadata")
@PreAuthorize("hasAnyRole('ADMIN', 'SUPER_ADMIN') or hasAuthority('SCOPE_internal')")
public class DocumentRepositoryController {

    private final EnterpriseSearchService searchService;

    @PostMapping("/index")
    @Operation(summary = "Index Document in Search Platform", description = "Indexes PDF, DOCX, XLSX, CSV, or Markdown document content excerpt and metadata into the search platform.")
    public ResponseEntity<SearchDocument> indexDocument(
            @RequestParam String indexName,
            @RequestParam String docExternalId,
            @RequestParam String docType,
            @RequestParam String title,
            @RequestParam String bodyExcerpt,
            @RequestParam(required = false) String tags,
            @RequestParam(required = false) String category,
            @RequestParam(required = false) UUID tenantId,
            @RequestParam(required = false) String sourceUrl) {
        return ResponseEntity.ok(searchService.indexDocument(indexName, docExternalId, docType, title, bodyExcerpt, tags, category, tenantId, sourceUrl));
    }
}
