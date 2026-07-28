package com.hyperlofy.backend.global.service;

import com.hyperlofy.backend.global.entity.*;
import com.hyperlofy.backend.global.repository.*;
import lombok.RequiredArgsConstructor;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.OffsetDateTime;
import java.util.List;
import java.util.UUID;

@Service
@RequiredArgsConstructor
public class GlobalInfrastructureService {

    private static final Logger log = LoggerFactory.getLogger(GlobalInfrastructureService.class);

    private final GlobalRegionRepository regionRepository;
    private final AvailabilityZoneRepository zoneRepository;
    private final DisasterRecoveryPlanRepository drPlanRepository;
    private final BackupExecutionRepository backupRepository;
    private final TrafficRoutingPolicyRepository routingRepository;

    @Transactional
    public GlobalRegion registerRegion(String regionCode, String regionName, String countryCode, String cloudProvider, boolean isPrimary) {
        log.info("[GLOBAL INFRASTRUCTURE] Registering global region Code={}, Name={}, Country={}", regionCode, regionName, countryCode);

        GlobalRegion region = regionRepository.findByRegionCode(regionCode).orElseGet(() ->
                GlobalRegion.builder()
                        .regionCode(regionCode)
                        .regionName(regionName)
                        .countryCode(countryCode)
                        .primaryCloudProvider(cloudProvider != null ? cloudProvider : "AWS")
                        .isPrimaryRegion(isPrimary)
                        .deploymentMode("ACTIVE")
                        .build()
        );

        return regionRepository.save(region);
    }

    @Transactional
    public DisasterRecoveryPlan createDrPlan(String planName, String primaryRegionCode, String targetDrRegionCode, Integer rpoSeconds, Integer rtoSeconds) {
        log.info("[GLOBAL INFRASTRUCTURE] Creating Disaster Recovery Plan Name={}, Primary={}, TargetDR={}", planName, primaryRegionCode, targetDrRegionCode);

        GlobalRegion primary = regionRepository.findByRegionCode(primaryRegionCode)
                .orElseThrow(() -> new IllegalArgumentException("Primary region not found: " + primaryRegionCode));
        GlobalRegion target = regionRepository.findByRegionCode(targetDrRegionCode)
                .orElseThrow(() -> new IllegalArgumentException("Target DR region not found: " + targetDrRegionCode));

        DisasterRecoveryPlan plan = drPlanRepository.findByPlanName(planName).orElseGet(() ->
                DisasterRecoveryPlan.builder()
                        .planName(planName)
                        .primaryRegion(primary)
                        .targetDrRegion(target)
                        .targetRpoSeconds(rpoSeconds != null ? rpoSeconds : 5)
                        .targetRtoSeconds(rtoSeconds != null ? rtoSeconds : 60)
                        .status("READY")
                        .build()
        );

        return drPlanRepository.save(plan);
    }

    @Transactional
    public DisasterRecoveryPlan executeFailover(String planName) {
        log.info("[GLOBAL INFRASTRUCTURE] Executing automated cross-region DR failover Plan={}", planName);

        DisasterRecoveryPlan plan = drPlanRepository.findByPlanName(planName)
                .orElseThrow(() -> new IllegalArgumentException("DR plan not found: " + planName));

        plan.setStatus("EXECUTING");
        plan.getPrimaryRegion().setDeploymentMode("PASSIVE");
        plan.getTargetDrRegion().setDeploymentMode("ACTIVE");
        plan.setStatus("RECOVERED");
        plan.setLastDrillAt(OffsetDateTime.now());

        regionRepository.save(plan.getPrimaryRegion());
        regionRepository.save(plan.getTargetDrRegion());

        return drPlanRepository.save(plan);
    }

    @Transactional
    public BackupExecution triggerBackup(String backupCode, String regionCode, String backupType, Long storageSizeBytes, String s3Uri) {
        log.info("[GLOBAL INFRASTRUCTURE] Triggering automated snapshot/WAL backup Code={}, Region={}, Type={}", backupCode, regionCode, backupType);

        BackupExecution backup = BackupExecution.builder()
                .backupCode(backupCode)
                .regionCode(regionCode)
                .backupType(backupType != null ? backupType : "FULL")
                .storageSizeBytes(storageSizeBytes != null ? storageSizeBytes : 10737418240L)
                .s3SnapshotUri(s3Uri != null ? s3Uri : "s3://hyperlofy-global-backups/" + regionCode + "/" + backupCode)
                .status("COMPLETED")
                .completedAt(OffsetDateTime.now())
                .build();

        return backupRepository.save(backup);
    }

    @Transactional
    public TrafficRoutingPolicy configureRouting(String policyName, String routingType, String targetRegionCode, Integer weightPercent) {
        log.info("[GLOBAL INFRASTRUCTURE] Configuring Geo DNS traffic policy Policy={}, Type={}, Region={}, Weight={}",
                policyName, routingType, targetRegionCode, weightPercent);

        TrafficRoutingPolicy policy = routingRepository.findByPolicyName(policyName).orElseGet(() ->
                TrafficRoutingPolicy.builder()
                        .policyName(policyName)
                        .routingType(routingType != null ? routingType : "GEO_LATENCY")
                        .targetRegionCode(targetRegionCode)
                        .trafficWeightPercent(weightPercent != null ? weightPercent : 100)
                        .healthStatus("HEALTHY")
                        .build()
        );

        return routingRepository.save(policy);
    }

    @Transactional(readOnly = true)
    public List<GlobalRegion> getAllRegions() {
        return regionRepository.findAll();
    }

    @Transactional(readOnly = true)
    public List<DisasterRecoveryPlan> getAllDrPlans() {
        return drPlanRepository.findAll();
    }

    @Transactional(readOnly = true)
    public List<BackupExecution> getAllBackups() {
        return backupRepository.findAll();
    }

    @Transactional(readOnly = true)
    public List<TrafficRoutingPolicy> getAllRoutingPolicies() {
        return routingRepository.findAll();
    }
}
