package com.hyperlofy.backend.observability.controller;

import com.hyperlofy.backend.observability.entity.ChaosExperiment;
import com.hyperlofy.backend.observability.entity.IncidentManagement;
import com.hyperlofy.backend.observability.entity.ObservabilityDashboard;
import com.hyperlofy.backend.observability.entity.ServiceCost;
import com.hyperlofy.backend.observability.service.ObservabilityEnterpriseService;
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
@RequestMapping("/api/v1/observability/enterprise")
@RequiredArgsConstructor
@Tag(name = "Enterprise Observability Platform Enterprise Addendum API", description = "Endpoints for Chaos Engineering experiments, major incident management & War Rooms, FinOps cost attribution, and Executive SLI/SLO dashboards")
@PreAuthorize("hasAnyRole('ADMIN', 'SUPER_ADMIN') or hasAuthority('SCOPE_internal')")
public class ObservabilityEnterpriseController {

    private final ObservabilityEnterpriseService enterpriseService;

    @PostMapping("/incidents")
    @Operation(summary = "Declare Major Incident & Auto-Provision War Room", description = "Declares major SEV1/SEV2 incident, assigns Incident Commander, and provisions virtual War Room bridge.")
    public ResponseEntity<IncidentManagement> declareIncident(
            @RequestParam String incidentCode,
            @RequestParam String title,
            @RequestParam(required = false) String severity,
            @RequestParam(required = false) UUID commanderUserId,
            @RequestParam(required = false) String warRoomUrl) {
        return ResponseEntity.ok(enterpriseService.declareMajorIncident(incidentCode, title, severity, commanderUserId, warRoomUrl));
    }

    @PostMapping("/chaos")
    @Operation(summary = "Execute Chaos Engineering Experiment", description = "Injects pod failure, network latency, or database failover to calculate service resilience scorecards.")
    public ResponseEntity<ChaosExperiment> runChaos(
            @RequestParam String experimentName,
            @RequestParam String experimentType,
            @RequestParam String targetService,
            @RequestParam(required = false) BigDecimal resilienceScore) {
        return ResponseEntity.ok(enterpriseService.runChaosExperiment(experimentName, experimentType, targetService, resilienceScore));
    }

    @PostMapping("/costs")
    @Operation(summary = "Record FinOps Cloud Cost Attribution", description = "Attributes monthly compute, storage, and networking cloud costs per microservice domain.")
    public ResponseEntity<ServiceCost> recordCost(
            @RequestParam String serviceName,
            @RequestParam(required = false) BigDecimal monthlyCost,
            @RequestParam(required = false) BigDecimal compute,
            @RequestParam(required = false) BigDecimal storage,
            @RequestParam(required = false) BigDecimal network) {
        return ResponseEntity.ok(enterpriseService.recordFinOpsCost(serviceName, monthlyCost, compute, storage, network));
    }

    @PostMapping("/dashboards")
    @Operation(summary = "Register Executive SLI/SLO Dashboard", description = "Registers executive Grafana dashboards monitoring SLO compliance, error budget burn, and business KPIs.")
    public ResponseEntity<ObservabilityDashboard> registerDashboard(
            @RequestParam String dashboardName,
            @RequestParam(required = false) String category,
            @RequestParam String grafanaUrl,
            @RequestParam(required = false) BigDecimal slo,
            @RequestParam(required = false) BigDecimal errorBudget) {
        return ResponseEntity.ok(enterpriseService.registerDashboard(dashboardName, category, grafanaUrl, slo, errorBudget));
    }

    @GetMapping("/incidents")
    @Operation(summary = "Get All Major Incidents", description = "Returns active and historical SEV1/SEV2 incidents and resolution timelines.")
    public ResponseEntity<List<IncidentManagement>> getIncidents() {
        return ResponseEntity.ok(enterpriseService.getAllIncidents());
    }

    @GetMapping("/resilience")
    @Operation(summary = "Get Chaos Resilience Scorecards", description = "Returns Chaos Engineering experiment history and resilience scores.")
    public ResponseEntity<List<ChaosExperiment>> getResilience() {
        return ResponseEntity.ok(enterpriseService.getAllChaosExperiments());
    }

    @GetMapping("/finops")
    @Operation(summary = "Get FinOps Cloud Cost Allocation", description = "Returns cloud compute/storage cost breakdowns across microservices.")
    public ResponseEntity<List<ServiceCost>> getFinOps() {
        return ResponseEntity.ok(enterpriseService.getAllFinOpsCosts());
    }

    @GetMapping("/dashboards")
    @Operation(summary = "Get Executive Observability Dashboards", description = "Lists executive Grafana dashboards, SLO targets, and error budget burn rates.")
    public ResponseEntity<List<ObservabilityDashboard>> getDashboards() {
        return ResponseEntity.ok(enterpriseService.getAllDashboards());
    }
}
