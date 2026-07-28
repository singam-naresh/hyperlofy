package com.hyperlofy.backend.data.controller;

import com.hyperlofy.backend.data.entity.DataDriftReport;
import com.hyperlofy.backend.data.entity.DataQualityResult;
import com.hyperlofy.backend.data.entity.DatasetRegistry;
import com.hyperlofy.backend.data.entity.TrainingDataset;
import com.hyperlofy.backend.data.service.DataEnterpriseService;
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
@RequestMapping("/api/v1/data/enterprise")
@RequiredArgsConstructor
@Tag(name = "Enterprise Data Platform Enterprise Addendum API", description = "Endpoints for Data Quality validation, MLOps training dataset generation, feature drift tracking, and dataset certification")
@PreAuthorize("hasAnyRole('ADMIN', 'SUPER_ADMIN') or hasAuthority('SCOPE_internal')")
public class DataEnterpriseController {

    private final DataEnterpriseService enterpriseService;

    @PostMapping("/quality")
    @Operation(summary = "Validate Data Quality & Quarantine", description = "Executes automated completeness, accuracy, and freshness quality rules, quarantining defective data.")
    public ResponseEntity<DataQualityResult> validateQuality(
            @RequestParam UUID datasetId,
            @RequestParam String ruleName,
            @RequestParam String ruleType,
            @RequestParam(required = false) Integer checked,
            @RequestParam(required = false) Integer failed) {
        return ResponseEntity.ok(enterpriseService.validateDataQuality(datasetId, ruleName, ruleType, checked, failed));
    }

    @PostMapping("/training")
    @Operation(summary = "Generate MLOps Training Dataset", description = "Creates versioned MLOps training snapshot datasets for AI model training pipelines.")
    public ResponseEntity<TrainingDataset> generateTraining(
            @RequestParam String datasetName,
            @RequestParam String modelType,
            @RequestParam String storagePath,
            @RequestParam(required = false) Integer sampleCount,
            @RequestParam(required = false) String featureVersion) {
        return ResponseEntity.ok(enterpriseService.generateTrainingDataset(datasetName, modelType, storagePath, sampleCount, featureVersion));
    }

    @PostMapping("/drift")
    @Operation(summary = "Record MLOps Feature & Data Drift", description = "Tracks feature distribution drift and dataset statistical anomalies for deployed AI models.")
    public ResponseEntity<DataDriftReport> recordDrift(
            @RequestParam String modelId,
            @RequestParam String featureName,
            @RequestParam(required = false) BigDecimal driftScore) {
        return ResponseEntity.ok(enterpriseService.recordDataDrift(modelId, featureName, driftScore));
    }

    @PostMapping("/certify")
    @Operation(summary = "Certify Enterprise Dataset", description = "Certifies dataset governance, data stewardship, and column-level classification levels.")
    public ResponseEntity<DatasetRegistry> certifyDataset(
            @RequestParam String datasetName,
            @RequestParam String datasetOwner,
            @RequestParam(required = false) String classificationLevel) {
        return ResponseEntity.ok(enterpriseService.certifyDataset(datasetName, datasetOwner, classificationLevel));
    }

    @GetMapping("/catalog")
    @Operation(summary = "Get Certified Dataset Catalog", description = "Returns enterprise metadata catalog of certified datasets, quality scores, and classification levels.")
    public ResponseEntity<List<DatasetRegistry>> getCatalog() {
        return ResponseEntity.ok(enterpriseService.getAllDatasets());
    }
}
