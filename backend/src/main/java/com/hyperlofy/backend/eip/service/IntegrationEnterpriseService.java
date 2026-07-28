package com.hyperlofy.backend.eip.service;

import com.hyperlofy.backend.eip.entity.B2bMessage;
import com.hyperlofy.backend.eip.entity.CdcStream;
import com.hyperlofy.backend.eip.entity.ConnectorMarketplace;
import com.hyperlofy.backend.eip.entity.MasterDataRegistry;
import com.hyperlofy.backend.eip.repository.B2bMessageRepository;
import com.hyperlofy.backend.eip.repository.CdcStreamRepository;
import com.hyperlofy.backend.eip.repository.ConnectorMarketplaceRepository;
import com.hyperlofy.backend.eip.repository.MasterDataRegistryRepository;
import lombok.RequiredArgsConstructor;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;
import java.util.UUID;

@Service
@RequiredArgsConstructor
public class IntegrationEnterpriseService {

    private static final Logger log = LoggerFactory.getLogger(IntegrationEnterpriseService.class);

    private final ConnectorMarketplaceRepository marketplaceRepository;
    private final CdcStreamRepository cdcRepository;
    private final B2bMessageRepository b2bRepository;
    private final MasterDataRegistryRepository mdmRepository;

    @Transactional
    public ConnectorMarketplace certifyConnectorTemplate(String templateName, String category, String publisher, String version) {
        log.info("[ENTERPRISE INTEGRATION] Certifying B2B connector marketplace template Name={}, Category={}, Version={}",
                templateName, category, version);

        ConnectorMarketplace template = marketplaceRepository.findByTemplateName(templateName).orElseGet(() ->
                ConnectorMarketplace.builder()
                        .templateName(templateName)
                        .category(category)
                        .publisher(publisher != null ? publisher : "HYPERLOFY_LABS")
                        .version(version != null ? version : "v1.0.0")
                        .certificationStatus("CERTIFIED")
                        .build()
        );

        return marketplaceRepository.save(template);
    }

    @Transactional
    public CdcStream configureCdcStream(String streamName, String sourceTable, String kafkaTopic) {
        log.info("[ENTERPRISE INTEGRATION] Configuring Debezium CDC Stream StreamName={}, SourceTable={}, KafkaTopic={}", streamName, sourceTable, kafkaTopic);

        CdcStream stream = cdcRepository.findByStreamName(streamName).orElseGet(() ->
                CdcStream.builder()
                        .streamName(streamName)
                        .sourceTable(sourceTable)
                        .kafkaTopic(kafkaTopic)
                        .status("STREAMING")
                        .lagMs(0L)
                        .build()
        );

        return cdcRepository.save(stream);
    }

    @Transactional
    public B2bMessage sendEdiMessage(UUID partnerId, String messageType, String controlNumber) {
        log.info("[ENTERPRISE INTEGRATION] Processing EDI X12/AS2 message PartnerId={}, Type={}, ControlNum={}", partnerId, messageType, controlNumber);

        B2bMessage message = b2bRepository.findByControlNumber(controlNumber).orElseGet(() ->
                B2bMessage.builder()
                        .partnerId(partnerId)
                        .messageType(messageType)
                        .controlNumber(controlNumber)
                        .status("ACKNOWLEDGED")
                        .encryptionType("AES256_RSA")
                        .build()
        );

        return b2bRepository.save(message);
    }

    @Transactional
    public MasterDataRegistry syncGoldenMasterRecord(String domainType, String masterCode, String goldenRecordJson) {
        log.info("[ENTERPRISE INTEGRATION] Synchronizing MDM Golden Record Domain={}, Code={}", domainType, masterCode);

        MasterDataRegistry record = mdmRepository.findByMasterCode(masterCode).orElseGet(() ->
                MasterDataRegistry.builder()
                        .domainType(domainType)
                        .masterCode(masterCode)
                        .goldenRecordJson(goldenRecordJson)
                        .version(1)
                        .status("ACTIVE")
                        .build()
        );

        record.setGoldenRecordJson(goldenRecordJson);
        record.setVersion(record.getVersion() + 1);

        return mdmRepository.save(record);
    }

    @Transactional(readOnly = true)
    public List<ConnectorMarketplace> getMarketplaceTemplates() {
        return marketplaceRepository.findAll();
    }
}
