package com.hyperlofy.backend.data.service;

import com.hyperlofy.backend.data.entity.DataDriftReport;
import com.hyperlofy.backend.data.entity.DataQualityResult;
import com.hyperlofy.backend.data.entity.DatasetRegistry;
import com.hyperlofy.backend.data.entity.TrainingDataset;
import com.hyperlofy.backend.data.repository.DataDriftReportRepository;
import com.hyperlofy.backend.data.repository.DataQualityResultRepository;
import com.hyperlofy.backend.data.repository.DatasetRegistryRepository;
import com.hyperlofy.backend.data.repository.TrainingDatasetRepository;
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
public class DataEnterpriseService {

    private static final Logger log = LoggerFactory.getLogger(DataEnterpriseService.class);

    private final DatasetRegistryRepository datasetRepository;
    private final DataQualityResultRepository qualityRepository;
    private final DataDriftReportRepository driftRepository;
    private final TrainingDatasetRepository trainingRepository;

    @Transactional
    public DataQualityResult validateDataQuality(UUID datasetId, String ruleName, String ruleType, Integer checked, Integer failed) {
        log.info("[DATA ENTERPRISE PLATFORM] Validating data quality DatasetId={}, Rule={}, Type={}, Checked={}, Failed={}",
                datasetId, ruleName, ruleType, checked, failed);

        String status = (failed != null && failed > 0) ? "QUARANTINED" : "PASSED";

        DataQualityResult result = DataQualityResult.builder()
                .datasetId(datasetId)
                .ruleName(ruleName)
                .ruleType(ruleType)
                .status(status)
                .recordsChecked(checked != null ? checked : 1000)
                .recordsFailed(failed != null ? failed : 0)
                .executedAt(OffsetDateTime.now())
                .build();

        return qualityRepository.save(result);
    }

    @Transactional
    public TrainingDataset generateTrainingDataset(String datasetName, String modelType, String storagePath, Integer sampleCount, String featureVersion) {
        log.info("[DATA ENTERPRISE PLATFORM] Generating MLOps training dataset Name={}, Model={}, Path={}, Samples={}",
                datasetName, modelType, storagePath, sampleCount);

        TrainingDataset dataset = trainingRepository.findByDatasetName(datasetName).orElseGet(() ->
                TrainingDataset.builder()
                        .datasetName(datasetName)
                        .modelType(modelType)
                        .storagePath(storagePath)
                        .sampleCount(sampleCount != null ? sampleCount : 1000000)
                        .featureVersion(featureVersion != null ? featureVersion : "v1.0")
                        .build()
        );

        return trainingRepository.save(dataset);
    }

    @Transactional
    public DataDriftReport recordDataDrift(String modelId, String featureName, BigDecimal driftScore) {
        log.info("[DATA ENTERPRISE PLATFORM] Recording MLOps feature/data drift Model={}, Feature={}, Score={}", modelId, featureName, driftScore);

        boolean detected = driftScore != null && driftScore.compareTo(new BigDecimal("0.0500")) > 0;

        DataDriftReport report = DataDriftReport.builder()
                .modelId(modelId)
                .featureName(featureName)
                .driftScore(driftScore != null ? driftScore : new BigDecimal("0.0125"))
                .driftDetected(detected)
                .reportTimestamp(OffsetDateTime.now())
                .build();

        return driftRepository.save(report);
    }

    @Transactional
    public DatasetRegistry certifyDataset(String datasetName, String datasetOwner, String classificationLevel) {
        log.info("[DATA ENTERPRISE PLATFORM] Certifying Enterprise Dataset Name={}, Owner={}, Level={}", datasetName, datasetOwner, classificationLevel);

        DatasetRegistry registry = datasetRepository.findByDatasetName(datasetName).orElseGet(() ->
                DatasetRegistry.builder()
                        .datasetName(datasetName)
                        .datasetOwner(datasetOwner)
                        .classificationLevel(classificationLevel != null ? classificationLevel : "CONFIDENTIAL")
                        .certificationStatus("CERTIFIED")
                        .qualityScore(new BigDecimal("99.50"))
                        .build()
        );

        return datasetRepository.save(registry);
    }

    @Transactional(readOnly = true)
    public List<DatasetRegistry> getAllDatasets() {
        return datasetRepository.findAll();
    }
}
