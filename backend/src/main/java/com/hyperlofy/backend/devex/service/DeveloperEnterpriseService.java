package com.hyperlofy.backend.devex.service;

import com.hyperlofy.backend.devex.entity.ApiContract;
import com.hyperlofy.backend.devex.entity.PartnerApplication;
import com.hyperlofy.backend.devex.entity.PartnerWebhook;
import com.hyperlofy.backend.devex.entity.ServiceScorecard;
import com.hyperlofy.backend.devex.repository.ApiContractRepository;
import com.hyperlofy.backend.devex.repository.PartnerApplicationRepository;
import com.hyperlofy.backend.devex.repository.PartnerWebhookRepository;
import com.hyperlofy.backend.devex.repository.ServiceScorecardRepository;
import lombok.RequiredArgsConstructor;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.math.BigDecimal;
import java.util.UUID;

@Service
@RequiredArgsConstructor
public class DeveloperEnterpriseService {

    private static final Logger log = LoggerFactory.getLogger(DeveloperEnterpriseService.class);

    private final ApiContractRepository contractRepository;
    private final PartnerApplicationRepository partnerRepository;
    private final PartnerWebhookRepository webhookRepository;
    private final ServiceScorecardRepository scorecardRepository;

    @Transactional
    public ApiContract registerConsumerContract(String contractName, String apiName, String consumerService, String schemaVersion) {
        log.info("[DEVELOPER ENTERPRISE] Registering consumer-driven API contract Contract={}, Api={}, Consumer={}",
                contractName, apiName, consumerService);

        ApiContract contract = contractRepository.findByContractName(contractName).orElseGet(() ->
                ApiContract.builder()
                        .contractName(contractName)
                        .apiName(apiName)
                        .consumerService(consumerService)
                        .schemaVersion(schemaVersion != null ? schemaVersion : "v1.0.0")
                        .validationStatus("VALIDATED")
                        .build()
        );

        return contractRepository.save(contract);
    }

    @Transactional
    public PartnerApplication registerPartnerApplication(String partnerName, String appName, String contactEmail) {
        log.info("[DEVELOPER ENTERPRISE] Registering external partner application Partner={}, App={}, Email={}", partnerName, appName, contactEmail);

        String clientId = "client_" + UUID.randomUUID().toString().replace("-", "").substring(0, 12);

        PartnerApplication app = PartnerApplication.builder()
                .partnerName(partnerName)
                .appName(appName)
                .clientId(clientId)
                .status("ACTIVE")
                .contactEmail(contactEmail)
                .build();

        return partnerRepository.save(app);
    }

    @Transactional
    public PartnerWebhook registerPartnerWebhook(UUID partnerAppId, String targetUrl, String eventType) {
        log.info("[DEVELOPER ENTERPRISE] Registering signed partner webhook AppId={}, Url={}, Event={}", partnerAppId, targetUrl, eventType);

        String secretKey = "whsec_" + UUID.randomUUID().toString().replace("-", "");

        PartnerWebhook webhook = PartnerWebhook.builder()
                .partnerAppId(partnerAppId)
                .targetUrl(targetUrl)
                .eventType(eventType)
                .secretKey(secretKey)
                .status("ACTIVE")
                .build();

        return webhookRepository.save(webhook);
    }

    @Transactional
    public ServiceScorecard computeServiceScorecard(String serviceName, BigDecimal security, BigDecimal observability, BigDecimal docs) {
        log.info("[DEVELOPER ENTERPRISE] Computing IDP service scorecard Service={}, Sec={}, Obs={}, Docs={}",
                serviceName, security, observability, docs);

        BigDecimal overall = security.add(observability).add(docs).divide(new BigDecimal("3.0"), 2, BigDecimal.ROUND_HALF_UP);

        ServiceScorecard scorecard = scorecardRepository.findByServiceName(serviceName).orElseGet(() ->
                ServiceScorecard.builder()
                        .serviceName(serviceName)
                        .build()
        );

        scorecard.setSecurityScore(security);
        scorecard.setObservabilityScore(observability);
        scorecard.setDocumentationScore(docs);
        scorecard.setOverallScore(overall);
        scorecard.setGrade(overall.compareTo(new BigDecimal("90.00")) >= 0 ? "A+" : "B");

        return scorecardRepository.save(scorecard);
    }
}
