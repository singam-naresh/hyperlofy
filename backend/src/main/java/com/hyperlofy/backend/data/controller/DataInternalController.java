package com.hyperlofy.backend.data.controller;

import com.hyperlofy.backend.data.entity.DataPipeline;
import com.hyperlofy.backend.data.entity.FeatureStore;
import com.hyperlofy.backend.data.entity.LakehouseTable;
import com.hyperlofy.backend.data.entity.StreamJob;
import com.hyperlofy.backend.data.service.EnterpriseDataPlatformService;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.tags.Tag;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/api/v1/data/internal")
@RequiredArgsConstructor
@Tag(name = "Enterprise Data Platform Internal API", description = "Endpoints for batch/streaming data pipeline registration, Flink/Kafka Streams launch, Iceberg Lakehouse management, and Feature Store ingestion")
@PreAuthorize("hasAnyRole('ADMIN', 'SUPER_ADMIN') or hasAuthority('SCOPE_internal')")
public class DataInternalController {

    private final EnterpriseDataPlatformService dataService;

    @PostMapping("/pipelines")
    @Operation(summary = "Register Batch/Streaming Ingestion Pipeline", description = "Configures real-time event streaming or batch ingestion pipeline into Bronze/Silver/Gold Lakehouse layers.")
    public ResponseEntity<DataPipeline> registerPipeline(
            @RequestParam String pipelineCode,
            @RequestParam String pipelineName,
            @RequestParam String pipelineType,
            @RequestParam String sourceSystem,
            @RequestParam(required = false) String targetLayer) {
        return ResponseEntity.ok(dataService.registerPipeline(pipelineCode, pipelineName, pipelineType, sourceSystem, targetLayer));
    }

    @PostMapping("/streams")
    @Operation(summary = "Launch Real-Time Streaming Analytics Job", description = "Deploys Kafka Streams or Apache Flink window aggregation job for real-time fraud detection and driver/merchant KPIs.")
    public ResponseEntity<StreamJob> launchStream(
            @RequestParam String jobName,
            @RequestParam(required = false) String engineType,
            @RequestParam String inputTopic,
            @RequestParam String outputTopic) {
        return ResponseEntity.ok(dataService.launchStreamJob(jobName, engineType, inputTopic, outputTopic));
    }

    @PostMapping("/features")
    @Operation(summary = "Ingest Online/Offline ML Feature", description = "Writes entity feature key-values into low-latency Redis/PostgreSQL Feature Store for real-time AI inference.")
    public ResponseEntity<FeatureStore> updateFeature(
            @RequestParam String entityType,
            @RequestParam String entityId,
            @RequestParam String featureName,
            @RequestParam String featureValue,
            @RequestParam(required = false) String featureVersion) {
        return ResponseEntity.ok(dataService.updateFeatureStore(entityType, entityId, featureName, featureValue, featureVersion));
    }

    @GetMapping("/catalog")
    @Operation(summary = "Get Iceberg Lakehouse Metadata Catalog", description = "Lists registered Apache Iceberg / Parquet tables across Bronze, Silver, and Gold data layers.")
    public ResponseEntity<List<LakehouseTable>> getCatalog() {
        return ResponseEntity.ok(dataService.getLakehouseCatalog());
    }
}
