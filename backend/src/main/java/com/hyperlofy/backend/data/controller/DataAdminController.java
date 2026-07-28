package com.hyperlofy.backend.data.controller;

import com.hyperlofy.backend.data.entity.FeatureStore;
import com.hyperlofy.backend.data.entity.LakehouseTable;
import com.hyperlofy.backend.data.service.EnterpriseDataPlatformService;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.tags.Tag;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/api/v1/data/admin")
@RequiredArgsConstructor
@Tag(name = "Enterprise Data Platform Admin API", description = "Endpoints for Principal Data Architects to inspect Lakehouse storage layers and ML feature stores")
@PreAuthorize("hasAnyRole('ADMIN', 'SUPER_ADMIN')")
public class DataAdminController {

    private final EnterpriseDataPlatformService dataService;

    @GetMapping("/lakehouse")
    @Operation(summary = "Inspect Apache Iceberg Lakehouse Storage", description = "Returns active Apache Iceberg table sizes, record counts, and partition specs.")
    public ResponseEntity<List<LakehouseTable>> inspectLakehouse() {
        return ResponseEntity.ok(dataService.getLakehouseCatalog());
    }

    @GetMapping("/features")
    @Operation(summary = "Inspect Entity Feature Store Records", description = "Returns online/offline ML features registered for specified entity type and ID.")
    public ResponseEntity<List<FeatureStore>> inspectFeatures(
            @RequestParam String entityType,
            @RequestParam String entityId) {
        return ResponseEntity.ok(dataService.getEntityFeatures(entityType, entityId));
    }
}
