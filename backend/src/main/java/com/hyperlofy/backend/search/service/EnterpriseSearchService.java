package com.hyperlofy.backend.search.service;

import com.hyperlofy.backend.search.entity.*;
import com.hyperlofy.backend.search.repository.*;
import lombok.RequiredArgsConstructor;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.math.BigDecimal;
import java.time.OffsetDateTime;
import java.util.List;
import java.util.UUID;

@Service
@RequiredArgsConstructor
public class EnterpriseSearchService {

    private static final Logger log = LoggerFactory.getLogger(EnterpriseSearchService.class);

    private final SearchIndexRepository indexRepository;
    private final SearchDocumentRepository documentRepository;
    private final SearchAnalyticsRepository analyticsRepository;
    private final SearchSuggestionRepository suggestionRepository;
    private final VectorEmbeddingRepository vectorRepository;

    @Transactional
    public SearchIndex createOrUpdateIndex(String indexName, String domain, String alias, UUID tenantId) {
        log.info("[ENTERPRISE SEARCH] Managing index Name={}, Domain={}, Alias={}", indexName, domain, alias);

        SearchIndex index = indexRepository.findByIndexName(indexName).orElseGet(() ->
                SearchIndex.builder()
                        .indexName(indexName)
                        .domain(domain)
                        .indexAlias(alias)
                        .status("ACTIVE")
                        .documentCount(0L)
                        .sizeBytes(0L)
                        .version(1)
                        .tenantId(tenantId)
                        .build()
        );

        return indexRepository.save(index);
    }

    @Transactional
    public SearchDocument indexDocument(String indexName, String docExternalId, String docType,
                                        String title, String bodyExcerpt, String tags,
                                        String category, UUID tenantId, String sourceUrl) {
        log.info("[ENTERPRISE SEARCH] Indexing document ExtId={}, Type={}, Title={}", docExternalId, docType, title);

        SearchIndex index = indexRepository.findByIndexName(indexName).orElseGet(() ->
                createOrUpdateIndex(indexName, docType, indexName + "_alias", tenantId)
        );

        SearchDocument doc = SearchDocument.builder()
                .index(index)
                .docExternalId(docExternalId)
                .docType(docType)
                .title(title)
                .bodyExcerpt(bodyExcerpt)
                .tags(tags)
                .category(category)
                .tenantId(tenantId)
                .sourceUrl(sourceUrl)
                .relevanceScore(new BigDecimal("1.0000"))
                .isPublished(true)
                .build();

        doc = documentRepository.save(doc);

        index.setDocumentCount(index.getDocumentCount() + 1);
        indexRepository.save(index);

        return doc;
    }

    @Transactional
    public List<SearchDocument> executeSearch(String queryText, String searchType, String domain, UUID tenantId, UUID userId) {
        long startTime = System.currentTimeMillis();
        log.info("[ENTERPRISE SEARCH] Executing search Query='{}', Type={}, Domain={}", queryText, searchType, domain);

        List<SearchDocument> results = documentRepository.fullTextSearch(queryText);
        long duration = System.currentTimeMillis() - startTime;

        SearchAnalytics analytics = SearchAnalytics.builder()
                .queryText(queryText)
                .domain(domain)
                .resultCount(results.size())
                .isZeroResult(results.isEmpty())
                .responseTimeMs((int) duration)
                .searchType(searchType != null ? searchType : "FULL_TEXT")
                .tenantId(tenantId)
                .userId(userId)
                .build();
        analyticsRepository.save(analytics);

        return results;
    }

    @Transactional(readOnly = true)
    public List<SearchSuggestion> getSuggestions(String queryText) {
        return suggestionRepository.findBySuggestionTextContainingIgnoreCase(queryText);
    }

    @Transactional(readOnly = true)
    public List<SearchSuggestion> getTrendingSearches() {
        return suggestionRepository.findBySuggestionTypeAndIsActiveTrue("TRENDING");
    }

    @Transactional
    public VectorEmbedding generateVectorEmbedding(String sourceDocId, String sourceDocType, String chunkText, String embeddingVector) {
        log.info("[ENTERPRISE SEARCH] Storing RAG chunk vector embedding DocId={}, ChunkLen={}", sourceDocId, chunkText.length());

        VectorEmbedding embedding = VectorEmbedding.builder()
                .sourceDocId(sourceDocId)
                .sourceDocType(sourceDocType)
                .chunkText(chunkText)
                .embeddingVector(embeddingVector)
                .embeddingModel("gemini-text-embedding")
                .embeddingDimension(768)
                .similarityScore(new BigDecimal("0.950000"))
                .build();

        return vectorRepository.save(embedding);
    }

    @Transactional
    public SearchIndex reindexDomain(String indexName) {
        log.info("[ENTERPRISE SEARCH] Reindexing domain Index={}", indexName);
        SearchIndex index = indexRepository.findByIndexName(indexName)
                .orElseThrow(() -> new IllegalArgumentException("Index not found: " + indexName));

        index.setStatus("REINDEXING");
        index.setVersion(index.getVersion() + 1);
        index.setLastReindexedAt(OffsetDateTime.now());
        index.setStatus("ACTIVE");

        return indexRepository.save(index);
    }

    @Transactional(readOnly = true)
    public List<SearchIndex> getAllIndexes() {
        return indexRepository.findAll();
    }
}
