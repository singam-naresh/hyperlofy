package com.hyperlofy.backend.data.service;

import com.hyperlofy.backend.data.entity.DataPipeline;
import com.hyperlofy.backend.data.entity.FeatureStore;
import com.hyperlofy.backend.data.entity.LakehouseTable;
import com.hyperlofy.backend.data.entity.StreamJob;
import com.hyperlofy.backend.data.repository.DataPipelineRepository;
import com.hyperlofy.backend.data.repository.FeatureStoreRepository;
import com.hyperlofy.backend.data.repository.LakehouseTableRepository;
import com.hyperlofy.backend.data.repository.StreamJobRepository;
import lombok.RequiredArgsConstructor;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;

@Service
@RequiredArgsConstructor
public class EnterpriseDataPlatformService {

    private static final Logger log = LoggerFactory.getLogger(EnterpriseDataPlatformService.class);

    private final DataPipelineRepository pipelineRepository;
    private final StreamJobRepository streamJobRepository;
    private final LakehouseTableRepository lakehouseRepository;
    private final FeatureStoreRepository featureStoreRepository;

    @Transactional
    public DataPipeline registerPipeline(String pipelineCode, String pipelineName, String pipelineType, String sourceSystem, String targetLayer) {
        log.info("[ENTERPRISE DATA PLATFORM] Registering data ingestion pipeline Code={}, Name={}, Type={}, Layer={}",
                pipelineCode, pipelineName, pipelineType, targetLayer);

        DataPipeline pipeline = pipelineRepository.findByPipelineCode(pipelineCode).orElseGet(() ->
                DataPipeline.builder()
                        .pipelineCode(pipelineCode)
                        .pipelineName(pipelineName)
                        .pipelineType(pipelineType)
                        .sourceSystem(sourceSystem)
                        .targetLayer(targetLayer != null ? targetLayer : "BRONZE")
                        .status("ACTIVE")
                        .build()
        );

        return pipelineRepository.save(pipeline);
    }

    @Transactional
    public StreamJob launchStreamJob(String jobName, String engineType, String inputTopic, String outputTopic) {
        log.info("[ENTERPRISE DATA PLATFORM] Launching real-time streaming job JobName={}, Engine={}, Input={}, Output={}",
                jobName, engineType, inputTopic, outputTopic);

        StreamJob job = streamJobRepository.findByJobName(jobName).orElseGet(() ->
                StreamJob.builder()
                        .jobName(jobName)
                        .engineType(engineType != null ? engineType : "KAFKA_STREAMS")
                        .inputTopic(inputTopic)
                        .outputTopic(outputTopic)
                        .status("RUNNING")
                        .throughputEps(5000)
                        .build()
        );

        return streamJobRepository.save(job);
    }

    @Transactional
    public LakehouseTable registerLakehouseTable(String tableName, String schemaNamespace, String lakehouseLayer, String format) {
        log.info("[ENTERPRISE DATA PLATFORM] Registering Apache Iceberg Lakehouse table Table={}, Namespace={}, Layer={}",
                tableName, schemaNamespace, lakehouseLayer);

        LakehouseTable table = lakehouseRepository.findByTableName(tableName).orElseGet(() ->
                LakehouseTable.builder()
                        .tableName(tableName)
                        .schemaNamespace(schemaNamespace != null ? schemaNamespace : "hyperlofy_lakehouse")
                        .lakehouseLayer(lakehouseLayer)
                        .format(format != null ? format : "ICEBERG_PARQUET")
                        .totalRecords(1000000L)
                        .sizeBytes(1073741824L) // 1 GB
                        .build()
        );

        return lakehouseRepository.save(table);
    }

    @Transactional
    public FeatureStore updateFeatureStore(String entityType, String entityId, String featureName, String featureValue, String featureVersion) {
        log.info("[ENTERPRISE DATA PLATFORM] Writing ML feature store record Type={}, Id={}, Feature={}, Value={}",
                entityType, entityId, featureName, featureValue);

        FeatureStore feature = featureStoreRepository.findByEntityTypeAndEntityIdAndFeatureName(entityType, entityId, featureName).orElseGet(() ->
                FeatureStore.builder()
                        .entityType(entityType)
                        .entityId(entityId)
                        .featureName(featureName)
                        .featureValue(featureValue)
                        .featureVersion(featureVersion != null ? featureVersion : "v1")
                        .build()
        );

        feature.setFeatureValue(featureValue);
        if (featureVersion != null) feature.setFeatureVersion(featureVersion);

        return featureStoreRepository.save(feature);
    }

    @Transactional(readOnly = true)
    public List<LakehouseTable> getLakehouseCatalog() {
        return lakehouseRepository.findAll();
    }

    @Transactional(readOnly = true)
    public List<FeatureStore> getEntityFeatures(String entityType, String entityId) {
        return featureStoreRepository.findByEntityTypeAndEntityId(entityType, entityId);
    }
}
