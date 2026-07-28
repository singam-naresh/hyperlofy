package com.hyperlofy.backend.search.controller;

import com.hyperlofy.backend.search.entity.SearchDocument;
import com.hyperlofy.backend.search.entity.SearchIndex;
import com.hyperlofy.backend.search.entity.SearchSuggestion;
import com.hyperlofy.backend.search.service.EnterpriseSearchService;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.tags.Tag;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.*;

import java.util.List;
import java.util.UUID;

@RestController
@RequestMapping("/api/v1/search")
@RequiredArgsConstructor
@Tag(name = "Enterprise Search & Intelligent Discovery API", description = "Lightning-fast full-text search, semantic AI vector search, hybrid search, autocomplete suggestions, and reindexing across all Hyperlofy domains")
@PreAuthorize("hasAnyRole('ADMIN', 'SUPER_ADMIN') or hasAuthority('SCOPE_internal')")
public class EnterpriseSearchController {

    private final EnterpriseSearchService searchService;

    @PostMapping
    @Operation(summary = "Execute Enterprise Search", description = "Executes full-text keyword search, semantic vector search, or hybrid search across merchants, products, orders, knowledge articles, and documents.")
    public ResponseEntity<List<SearchDocument>> search(
            @RequestParam String queryText,
            @RequestParam(required = false, defaultValue = "FULL_TEXT") String searchType,
            @RequestParam(required = false) String domain,
            @RequestParam(required = false) UUID tenantId,
            @RequestParam(required = false) UUID userId) {
        return ResponseEntity.ok(searchService.executeSearch(queryText, searchType, domain, tenantId, userId));
    }

    @GetMapping("/suggestions")
    @Operation(summary = "Get Search Autocomplete Suggestions", description = "Returns instant search autocomplete suggestions and query completions for user input.")
    public ResponseEntity<List<SearchSuggestion>> getSuggestions(@RequestParam String queryText) {
        return ResponseEntity.ok(searchService.getSuggestions(queryText));
    }

    @GetMapping("/trending")
    @Operation(summary = "Get Trending & Popular Searches", description = "Lists top trending queries, popular searches, and recent search patterns across the platform.")
    public ResponseEntity<List<SearchSuggestion>> getTrending() {
        return ResponseEntity.ok(searchService.getTrendingSearches());
    }

    @PostMapping("/reindex")
    @Operation(summary = "Trigger Reindexing for Domain Index", description = "Initiates zero-downtime rolling reindexing for a specific search domain (MERCHANTS, PRODUCTS, ORDERS, KNOWLEDGE).")
    public ResponseEntity<SearchIndex> reindex(@RequestParam String indexName) {
        return ResponseEntity.ok(searchService.reindexDomain(indexName));
    }

    @GetMapping("/indexes")
    @Operation(summary = "List All Enterprise Search Indexes", description = "Returns index lifecycle status (ACTIVE, REINDEXING, WARM, COLD), document counts, and sizes for all domain indexes.")
    public ResponseEntity<List<SearchIndex>> getIndexes() {
        return ResponseEntity.ok(searchService.getAllIndexes());
    }
}
