package com.hyperlofy.backend.search.service;

import com.hyperlofy.backend.search.entity.*;
import com.hyperlofy.backend.search.repository.*;
import lombok.RequiredArgsConstructor;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.math.BigDecimal;
import java.util.List;
import java.util.UUID;

@Service
@RequiredArgsConstructor
public class SearchEnterpriseService {

    private static final Logger log = LoggerFactory.getLogger(SearchEnterpriseService.class);

    private final KnowledgeGraphNodeRepository nodeRepository;
    private final KnowledgeGraphEdgeRepository edgeRepository;
    private final SearchConversationRepository conversationRepository;
    private final RagChunkRepository ragChunkRepository;
    private final SearchGovernanceRepository governanceRepository;

    @Transactional
    public KnowledgeGraphNode addNode(String entityId, String entityType, String entityName, String metadata, UUID tenantId) {
        log.info("[SEARCH ENTERPRISE] Adding Knowledge Graph node EntityId={}, Type={}, Name={}", entityId, entityType, entityName);

        KnowledgeGraphNode node = nodeRepository.findByEntityId(entityId).orElseGet(() ->
                KnowledgeGraphNode.builder()
                        .entityId(entityId)
                        .entityType(entityType)
                        .entityName(entityName)
                        .metadata(metadata)
                        .tenantId(tenantId)
                        .build()
        );

        return nodeRepository.save(node);
    }

    @Transactional
    public KnowledgeGraphEdge addEdge(UUID sourceNodeId, UUID targetNodeId, String relationshipType, BigDecimal weight, UUID tenantId) {
        log.info("[SEARCH ENTERPRISE] Adding Knowledge Graph edge Source={}, Target={}, Rel={}", sourceNodeId, targetNodeId, relationshipType);

        KnowledgeGraphNode source = nodeRepository.findById(sourceNodeId)
                .orElseThrow(() -> new IllegalArgumentException("Source node not found: " + sourceNodeId));
        KnowledgeGraphNode target = nodeRepository.findById(targetNodeId)
                .orElseThrow(() -> new IllegalArgumentException("Target node not found: " + targetNodeId));

        KnowledgeGraphEdge edge = KnowledgeGraphEdge.builder()
                .sourceNode(source)
                .targetNode(target)
                .relationshipType(relationshipType)
                .weight(weight != null ? weight : new BigDecimal("1.0000"))
                .tenantId(tenantId)
                .build();

        return edgeRepository.save(edge);
    }

    @Transactional
    public SearchConversation executeAiChat(String conversationCode, UUID userId, String userQuery, String tenantIdStr) {
        log.info("[SEARCH ENTERPRISE] Executing Conversational AI Search User={}, Query='{}'", userId, userQuery);

        SearchConversation conversation = conversationRepository.findByConversationCode(conversationCode).orElseGet(() ->
                SearchConversation.builder()
                        .conversationCode(conversationCode)
                        .userId(userId)
                        .userQuery(userQuery)
                        .aiResponse("Found relevant business objects based on your query: " + userQuery)
                        .intentType("SEARCH")
                        .confidenceScore(new BigDecimal("95.50"))
                        .citationSources("DOC-101, KA-202")
                        .build()
        );

        return conversationRepository.save(conversation);
    }

    @Transactional
    public RagChunk indexRagChunk(String sourceId, String sourceType, Integer chunkIndex, String chunkText, String vectorEmbedding) {
        log.info("[SEARCH ENTERPRISE] Indexing RAG Chunk Source={}, Type={}, Index={}", sourceId, sourceType, chunkIndex);

        RagChunk chunk = RagChunk.builder()
                .sourceId(sourceId)
                .sourceType(sourceType)
                .chunkIndex(chunkIndex != null ? chunkIndex : 0)
                .chunkText(chunkText)
                .vectorEmbedding(vectorEmbedding)
                .relevanceScore(new BigDecimal("0.9500"))
                .build();

        return ragChunkRepository.save(chunk);
    }

    @Transactional
    public SearchGovernance classifyDocument(String documentId, String sensitivityLevel, String classification, UUID ownerUserId, String accessRoles) {
        log.info("[SEARCH ENTERPRISE] Classifying document governance DocId={}, Sensitivity={}, Roles={}", documentId, sensitivityLevel, accessRoles);

        SearchGovernance governance = governanceRepository.findByDocumentId(documentId).orElseGet(() ->
                SearchGovernance.builder()
                        .documentId(documentId)
                        .sensitivityLevel(sensitivityLevel != null ? sensitivityLevel : "INTERNAL")
                        .classification(classification != null ? classification : "STANDARD")
                        .ownerUserId(ownerUserId)
                        .accessRoles(accessRoles != null ? accessRoles : "ROLE_USER")
                        .build()
        );

        return governanceRepository.save(governance);
    }

    @Transactional(readOnly = true)
    public List<KnowledgeGraphEdge> getGraphNeighbours(UUID nodeId) {
        return edgeRepository.findBySourceNode_Id(nodeId);
    }

    @Transactional(readOnly = true)
    public SearchGovernance getGovernanceByDocId(String documentId) {
        return governanceRepository.findByDocumentId(documentId)
                .orElseThrow(() -> new IllegalArgumentException("Governance record not found for document: " + documentId));
    }
}
