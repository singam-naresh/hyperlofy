package com.hyperlofy.backend.search.controller;

import com.hyperlofy.backend.search.entity.KnowledgeGraphEdge;
import com.hyperlofy.backend.search.entity.KnowledgeGraphNode;
import com.hyperlofy.backend.search.service.SearchEnterpriseService;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.tags.Tag;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.*;

import java.math.BigDecimal;
import java.util.List;
import java.util.UUID;

@RestController
@RequestMapping("/api/v1/graph")
@RequiredArgsConstructor
@Tag(name = "Enterprise Knowledge Graph API", description = "Connect enterprise business entities (Merchants, Products, Orders, Customers, Workflows, Cases) via semantic graph edges")
@PreAuthorize("hasAnyRole('ADMIN', 'SUPER_ADMIN') or hasAuthority('SCOPE_internal')")
public class KnowledgeGraphController {

    private final SearchEnterpriseService enterpriseService;

    @PostMapping("/node")
    @Operation(summary = "Add Knowledge Graph Node", description = "Registers business entity node (MERCHANT, PRODUCT, CUSTOMER, ORDER, WORKFLOW, CASE) in the enterprise Knowledge Graph.")
    public ResponseEntity<KnowledgeGraphNode> addNode(
            @RequestParam String entityId,
            @RequestParam String entityType,
            @RequestParam String entityName,
            @RequestParam(required = false) String metadata,
            @RequestParam(required = false) UUID tenantId) {
        return ResponseEntity.ok(enterpriseService.addNode(entityId, entityType, entityName, metadata, tenantId));
    }

    @PostMapping("/edge")
    @Operation(summary = "Add Knowledge Graph Relationship Edge", description = "Creates semantic relationship edge (PURCHASED, VIEWED, BELONGS_TO, ASSIGNED_TO, CREATED_BY) between graph nodes.")
    public ResponseEntity<KnowledgeGraphEdge> addEdge(
            @RequestParam UUID sourceNodeId,
            @RequestParam UUID targetNodeId,
            @RequestParam String relationshipType,
            @RequestParam(required = false) BigDecimal weight,
            @RequestParam(required = false) UUID tenantId) {
        return ResponseEntity.ok(enterpriseService.addEdge(sourceNodeId, targetNodeId, relationshipType, weight, tenantId));
    }

    @GetMapping("/neighbours")
    @Operation(summary = "Get Node Graph Neighbours", description = "Returns adjacent connected graph edges and entity relationships for specified node ID.")
    public ResponseEntity<List<KnowledgeGraphEdge>> getNeighbours(@RequestParam UUID nodeId) {
        return ResponseEntity.ok(enterpriseService.getGraphNeighbours(nodeId));
    }
}
