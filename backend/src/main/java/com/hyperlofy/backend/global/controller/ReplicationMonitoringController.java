package com.hyperlofy.backend.global.controller;

import com.hyperlofy.backend.global.entity.GlobalRegion;
import com.hyperlofy.backend.global.service.GlobalInfrastructureService;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.tags.Tag;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/api/v1/replication")
@RequiredArgsConstructor
@Tag(name = "Cross-Region Replication Monitoring API", description = "Monitor PostgreSQL WAL streaming replication, Kafka MirrorMaker 2 topic replication, and Redis cluster replication lag across regions")
@PreAuthorize("hasAnyRole('ADMIN', 'SUPER_ADMIN') or hasAuthority('SCOPE_internal')")
public class ReplicationMonitoringController {

    private final GlobalInfrastructureService globalService;

    @GetMapping("/database")
    @Operation(summary = "Get PostgreSQL Cross-Region Replication Status", description = "Returns PostgreSQL streaming & logical replication lag (seconds), WAL shipping status, and read replica health.")
    public ResponseEntity<List<GlobalRegion>> getDbReplication() {
        return ResponseEntity.ok(globalService.getAllRegions());
    }

    @GetMapping("/kafka")
    @Operation(summary = "Get Kafka MirrorMaker 2 Replication Status", description = "Returns Kafka MirrorMaker 2 cross-region topic replication lag, producer/consumer failover readiness, and DLQ status.")
    public ResponseEntity<List<GlobalRegion>> getKafkaReplication() {
        return ResponseEntity.ok(globalService.getAllRegions());
    }

    @GetMapping("/redis")
    @Operation(summary = "Get Redis Global Cluster Replication Status", description = "Returns Redis Sentinel / Cluster cross-region cache & session replication status, Sentinel quorum, and failover latency.")
    public ResponseEntity<List<GlobalRegion>> getRedisReplication() {
        return ResponseEntity.ok(globalService.getAllRegions());
    }
}
