package com.hyperlofy.backend.sre.service;

import com.hyperlofy.backend.sre.entity.PlatformDeployment;
import com.hyperlofy.backend.sre.entity.PlatformHealth;
import com.hyperlofy.backend.sre.entity.PlatformIncident;
import com.hyperlofy.backend.sre.entity.PlatformSlo;
import com.hyperlofy.backend.sre.repository.PlatformDeploymentRepository;
import com.hyperlofy.backend.sre.repository.PlatformHealthRepository;
import com.hyperlofy.backend.sre.repository.PlatformIncidentRepository;
import com.hyperlofy.backend.sre.repository.PlatformSloRepository;
import lombok.RequiredArgsConstructor;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.math.BigDecimal;
import java.time.ZonedDateTime;
import java.util.List;
import java.util.UUID;

@Service
@RequiredArgsConstructor
public class PlatformOperationsService {

    private static final Logger log = LoggerFactory.getLogger(PlatformOperationsService.class);

    private final PlatformHealthRepository healthRepository;
    private final PlatformDeploymentRepository deploymentRepository;
    private final PlatformIncidentRepository incidentRepository;
    private final PlatformSloRepository sloRepository;

    @Transactional
    public PlatformHealth updateHealthProbe(String serviceName, String status, BigDecimal cpuPct, BigDecimal memPct, Integer latencyMs) {
        log.info("[SRE PLATFORM] Probing service health: Service={}, Status={}, CPU={}%, Mem={}%, Latency={}ms",
                serviceName, status, cpuPct, memPct, latencyMs);

        PlatformHealth health = healthRepository.findByServiceName(serviceName).orElseGet(() ->
                PlatformHealth.builder()
                        .serviceName(serviceName)
                        .build()
        );

        health.setHealthStatus(status != null ? status : "HEALTHY");
        if (cpuPct != null) health.setCpuUtilizationPct(cpuPct);
        if (memPct != null) health.setMemoryUtilizationPct(memPct);
        if (latencyMs != null) health.setP95LatencyMs(latencyMs);
        health.setLastProbeAt(ZonedDateTime.now());

        return healthRepository.save(health);
    }

    @Transactional
    public PlatformDeployment triggerDeployment(String serviceName, String version, String strategy, String deployedBy) {
        log.info("[SRE PLATFORM] Deploying service: Service={}, Version={}, Strategy={}, DeployedBy={}",
                serviceName, version, strategy, deployedBy);

        String deployNo = "DEP-" + UUID.randomUUID().toString().replace("-", "").substring(0, 8).toUpperCase();

        PlatformDeployment deployment = PlatformDeployment.builder()
                .deploymentNumber(deployNo)
                .serviceName(serviceName)
                .version(version)
                .strategy(strategy != null ? strategy : "CANARY")
                .status("COMPLETED")
                .deployedBy(deployedBy)
                .build();

        return deploymentRepository.save(deployment);
    }

    @Transactional
    public PlatformIncident openIncident(String serviceName, String severity, String description) {
        log.info("[SRE PLATFORM] Opening SRE incident: Service={}, Severity={}, Description={}", serviceName, severity, description);

        String incidentNo = "INC-SRE-" + UUID.randomUUID().toString().replace("-", "").substring(0, 8).toUpperCase();

        PlatformIncident incident = PlatformIncident.builder()
                .incidentNumber(incidentNo)
                .serviceName(serviceName)
                .severity(severity != null ? severity : "SEV2")
                .description(description)
                .status("OPEN")
                .build();

        return incidentRepository.save(incident);
    }

    @Transactional(readOnly = true)
    public List<PlatformHealth> getOverallPlatformHealth() {
        return healthRepository.findAll();
    }

    @Transactional(readOnly = true)
    public List<PlatformSlo> getSloCompliance() {
        return sloRepository.findAll();
    }
}
