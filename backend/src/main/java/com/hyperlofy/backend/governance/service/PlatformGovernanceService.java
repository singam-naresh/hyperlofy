package com.hyperlofy.backend.governance.service;

import com.hyperlofy.backend.governance.entity.*;
import com.hyperlofy.backend.governance.repository.*;
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
public class PlatformGovernanceService {

    private static final Logger log = LoggerFactory.getLogger(PlatformGovernanceService.class);

    private final ArchitectureDecisionRecordRepository adrRepository;
    private final PlatformStandardRepository standardRepository;
    private final QualityGateExecutionRepository gateRepository;
    private final TechnicalDebtItemRepository debtRepository;
    private final ProductionCertificationRepository certificationRepository;

    @Transactional
    public ArchitectureDecisionRecord createAdr(String adrCode, String title, UUID authorUserId, String context, String decision, String consequences) {
        log.info("[PLATFORM GOVERNANCE] Registering ADR Code={}, Title={}", adrCode, title);

        ArchitectureDecisionRecord adr = adrRepository.findByAdrCode(adrCode).orElseGet(() ->
                ArchitectureDecisionRecord.builder()
                        .adrCode(adrCode)
                        .title(title)
                        .authorUserId(authorUserId)
                        .context(context)
                        .decision(decision)
                        .consequences(consequences)
                        .status("APPROVED")
                        .build()
        );

        return adrRepository.save(adr);
    }

    @Transactional
    public PlatformStandard registerStandard(String standardKey, String standardName, String category, String description) {
        log.info("[PLATFORM GOVERNANCE] Registering platform architecture standard Key={}, Category={}", standardKey, category);

        PlatformStandard standard = standardRepository.findByStandardKey(standardKey).orElseGet(() ->
                PlatformStandard.builder()
                        .standardKey(standardKey)
                        .standardName(standardName)
                        .category(category)
                        .description(description)
                        .complianceScore(new BigDecimal("100.00"))
                        .isMandatory(true)
                        .build()
        );

        return standardRepository.save(standard);
    }

    @Transactional
    public QualityGateExecution runQualityGate(String executionCode, String gateName, Integer totalChecks, Integer passedChecks, String summary) {
        log.info("[PLATFORM GOVERNANCE] Running quality gate execution Code={}, Gate={}, Passed={}/{}", executionCode, gateName, passedChecks, totalChecks);

        int failed = (totalChecks != null ? totalChecks : 10) - (passedChecks != null ? passedChecks : 10);
        String status = failed == 0 ? "PASSED" : "FAILED";

        QualityGateExecution execution = gateRepository.findByExecutionCode(executionCode).orElseGet(() ->
                QualityGateExecution.builder()
                        .executionCode(executionCode)
                        .gateName(gateName)
                        .totalChecks(totalChecks != null ? totalChecks : 10)
                        .passedChecks(passedChecks != null ? passedChecks : 10)
                        .failedChecks(failed)
                        .status(status)
                        .executionSummary(summary != null ? summary : "All quality gate checks passed cleanly.")
                        .build()
        );

        return gateRepository.save(execution);
    }

    @Transactional
    public ProductionCertification generateCertification(String certificationCode, String notes) {
        log.info("[PLATFORM GOVERNANCE] Issuing 10-Pillar Enterprise Production Certification Code={}", certificationCode);

        ProductionCertification cert = certificationRepository.findByCertificationCode(certificationCode).orElseGet(() ->
                ProductionCertification.builder()
                        .certificationCode(certificationCode)
                        .platformVersion("1.0.0-SNAPSHOT")
                        .certifiedBy("Chief Enterprise Architect")
                        .architectureCertified(true)
                        .securityCertified(true)
                        .performanceCertified(true)
                        .reliabilityCertified(true)
                        .complianceCertified(true)
                        .dataCertified(true)
                        .apiCertified(true)
                        .infrastructureCertified(true)
                        .operationsCertified(true)
                        .overallStatus("PRODUCTION_READY")
                        .certificationNotes(notes != null ? notes : "Hyperlofy Backend Platform fully certified for production deployment.")
                        .certifiedAt(OffsetDateTime.now())
                        .build()
        );

        return certificationRepository.save(cert);
    }

    @Transactional(readOnly = true)
    public List<ArchitectureDecisionRecord> getAllAdrs() {
        return adrRepository.findAll();
    }

    @Transactional(readOnly = true)
    public List<PlatformStandard> getAllStandards() {
        return standardRepository.findAll();
    }

    @Transactional(readOnly = true)
    public List<QualityGateExecution> getAllQualityGates() {
        return gateRepository.findAll();
    }

    @Transactional(readOnly = true)
    public List<TechnicalDebtItem> getAllTechnicalDebt() {
        return debtRepository.findAll();
    }

    @Transactional(readOnly = true)
    public List<ProductionCertification> getAllCertifications() {
        return certificationRepository.findAll();
    }
}
