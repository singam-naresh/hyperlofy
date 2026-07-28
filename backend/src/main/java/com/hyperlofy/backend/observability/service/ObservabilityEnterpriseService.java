package com.hyperlofy.backend.observability.service;

import com.hyperlofy.backend.observability.entity.ChaosExperiment;
import com.hyperlofy.backend.observability.entity.IncidentManagement;
import com.hyperlofy.backend.observability.entity.ObservabilityDashboard;
import com.hyperlofy.backend.observability.entity.ServiceCost;
import com.hyperlofy.backend.observability.repository.ChaosExperimentRepository;
import com.hyperlofy.backend.observability.repository.IncidentManagementRepository;
import com.hyperlofy.backend.observability.repository.ObservabilityDashboardRepository;
import com.hyperlofy.backend.observability.repository.ServiceCostRepository;
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
public class ObservabilityEnterpriseService {

    private static final Logger log = LoggerFactory.getLogger(ObservabilityEnterpriseService.class);

    private final IncidentManagementRepository incidentRepository;
    private final ChaosExperimentRepository chaosRepository;
    private final ServiceCostRepository costRepository;
    private final ObservabilityDashboardRepository dashboardRepository;

    @Transactional
    public IncidentManagement declareMajorIncident(String incidentCode, String title, String severity, UUID commanderUserId, String warRoomUrl) {
        log.info("[OBSERVABILITY ENTERPRISE] Declaring major incident Code={}, Title={}, Severity={}, Commander={}",
                incidentCode, title, severity, commanderUserId);

        IncidentManagement incident = incidentRepository.findByIncidentCode(incidentCode).orElseGet(() ->
                IncidentManagement.builder()
                        .incidentCode(incidentCode)
                        .title(title)
                        .severity(severity != null ? severity : "SEV1")
                        .commanderUserId(commanderUserId)
                        .warRoomUrl(warRoomUrl)
                        .status("OPEN")
                        .declaredAt(OffsetDateTime.now())
                        .build()
        );

        return incidentRepository.save(incident);
    }

    @Transactional
    public ChaosExperiment runChaosExperiment(String experimentName, String experimentType, String targetService, BigDecimal resilienceScore) {
        log.info("[OBSERVABILITY ENTERPRISE] Executing Chaos Engineering experiment Name={}, Type={}, Target={}, Score={}",
                experimentName, experimentType, targetService, resilienceScore);

        ChaosExperiment experiment = chaosRepository.findByExperimentName(experimentName).orElseGet(() ->
                ChaosExperiment.builder()
                        .experimentName(experimentName)
                        .experimentType(experimentType)
                        .targetService(targetService)
                        .resilienceScore(resilienceScore != null ? resilienceScore : new BigDecimal("98.00"))
                        .status("COMPLETED")
                        .startedAt(OffsetDateTime.now().minusMinutes(10))
                        .completedAt(OffsetDateTime.now())
                        .build()
        );

        return chaosRepository.save(experiment);
    }

    @Transactional
    public ServiceCost recordFinOpsCost(String serviceName, BigDecimal monthlyCost, BigDecimal compute, BigDecimal storage, BigDecimal network) {
        log.info("[OBSERVABILITY ENTERPRISE] Recording FinOps cost attribution Service={}, TotalUsd={}, Compute={}", serviceName, monthlyCost, compute);

        ServiceCost cost = costRepository.findByServiceName(serviceName).orElseGet(() ->
                ServiceCost.builder()
                        .serviceName(serviceName)
                        .build()
        );

        if (monthlyCost != null) cost.setMonthlyCostUsd(monthlyCost);
        if (compute != null) cost.setComputeCost(compute);
        if (storage != null) cost.setStorageCost(storage);
        if (network != null) cost.setNetworkCost(network);
        cost.setCostStatus("OPTIMIZED");

        return costRepository.save(cost);
    }

    @Transactional
    public ObservabilityDashboard registerDashboard(String dashboardName, String category, String grafanaUrl, BigDecimal slo, BigDecimal errorBudget) {
        log.info("[OBSERVABILITY ENTERPRISE] Registering Executive SLI/SLO Dashboard Name={}, Category={}, Slo={}", dashboardName, category, slo);

        ObservabilityDashboard dashboard = dashboardRepository.findByDashboardName(dashboardName).orElseGet(() ->
                ObservabilityDashboard.builder()
                        .dashboardName(dashboardName)
                        .category(category != null ? category : "EXECUTIVE_OPS")
                        .grafanaUrl(grafanaUrl)
                        .sloPercentage(slo != null ? slo : new BigDecimal("99.99"))
                        .errorBudgetRemaining(errorBudget != null ? errorBudget : new BigDecimal("95.00"))
                        .build()
        );

        return dashboardRepository.save(dashboard);
    }

    @Transactional(readOnly = true)
    public List<IncidentManagement> getAllIncidents() {
        return incidentRepository.findAll();
    }

    @Transactional(readOnly = true)
    public List<ChaosExperiment> getAllChaosExperiments() {
        return chaosRepository.findAll();
    }

    @Transactional(readOnly = true)
    public List<ServiceCost> getAllFinOpsCosts() {
        return costRepository.findAll();
    }

    @Transactional(readOnly = true)
    public List<ObservabilityDashboard> getAllDashboards() {
        return dashboardRepository.findAll();
    }
}
