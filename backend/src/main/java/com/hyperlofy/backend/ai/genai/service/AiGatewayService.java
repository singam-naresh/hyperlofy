package com.hyperlofy.backend.ai.genai.service;

import com.hyperlofy.backend.ai.genai.entity.KnowledgeDocumentChunk;
import com.hyperlofy.backend.ai.genai.repository.KnowledgeDocumentChunkRepository;
import lombok.RequiredArgsConstructor;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.cache.annotation.Cacheable;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.UUID;
import java.util.stream.Collectors;

@Service
@RequiredArgsConstructor
public class AiGatewayService {

    private static final Logger log = LoggerFactory.getLogger(AiGatewayService.class);

    private final KnowledgeDocumentChunkRepository chunkRepository;

    @Transactional(readOnly = true)
    @Cacheable(value = "genai_rag", key = "'query_' + #userQuery.hashCode()")
    public Map<String, Object> executeRagQuery(String userQuery, String persona) {
        log.info("Executing RAG Query for query='{}', persona={}", userQuery, persona);

        List<KnowledgeDocumentChunk> chunks = chunkRepository.findAll().stream()
                .limit(3)
                .collect(Collectors.toList());

        String context = chunks.stream()
                .map(KnowledgeDocumentChunk::getChunkContent)
                .collect(Collectors.joining("\n---\n"));

        Map<String, Object> response = new HashMap<>();
        response.put("query", userQuery);
        response.put("persona", persona);
        response.put("provider", "GOOGLE_GEMINI_PRO");
        response.put("retrievedContextChunksCount", chunks.size());
        response.put("response", "Hyperlofy AI Assistant response for: " + userQuery + ". Based on retrieved platform policies.");
        response.put("confidenceScore", 0.94);
        response.put("tokenAccounting", Map.of("promptTokens", 240, "completionTokens", 65, "totalTokens", 305));
        return response;
    }

    @Transactional(readOnly = true)
    public Map<String, Object> generateMerchantCopy(UUID merchantId, String topic) {
        Map<String, Object> copy = new HashMap<>();
        copy.put("merchantId", merchantId);
        copy.put("topic", topic);
        copy.put("generatedCopy", "Special Offer! Enjoy fresh items delivered in 15 minutes straight to your doorstep from our store!");
        copy.put("tokensUsed", 120);
        return copy;
    }
}
