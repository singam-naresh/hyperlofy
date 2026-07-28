package com.hyperlofy.backend.global.controller;

import com.hyperlofy.backend.global.entity.MultiCloudDeployment;
import com.hyperlofy.backend.global.service.GlobalEnterpriseService;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.tags.Tag;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/api/v1/multicloud")
@RequiredArgsConstructor
@Tag(name = "Multi-Cloud Workload Infrastructure API", description = "Govern workloads across AWS, Azure, and Google Cloud — cross-cloud failover, cloud bursting, provider health, and cost comparison")
@PreAuthorize("hasAnyRole('ADMIN', 'SUPER_ADMIN') or hasAuthority('SCOPE_internal')")
public class MultiCloudController {

    private final GlobalEnterpriseService enterpriseService;

    @PostMapping("/deploy")
    @Operation(summary = "Deploy Multi-Cloud Workload", description = "Deploys workload across AWS, Azure, or Google Cloud providers with cluster versioning and cost tracking.")
    public ResponseEntity<MultiCloudDeployment> deploy(
            @RequestParam String deploymentCode,
            @RequestParam String serviceName,
            @RequestParam String cloudProvider,
            @RequestParam String regionCode) {
        return ResponseEntity.ok(enterpriseService.deployMultiCloud(deploymentCode, serviceName, cloudProvider, regionCode));
    }

    @GetMapping("/providers")
    @Operation(summary = "List Active Multi-Cloud Deployments", description = "Returns active deployments across AWS, Azure, GCP, cluster Kubernetes versions, and monthly cloud costs.")
    public ResponseEntity<List<MultiCloudDeployment>> getProviders() {
        return ResponseEntity.ok(enterpriseService.getMultiCloudDeployments());
    }
}
