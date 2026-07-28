package com.hyperlofy.backend.platform.controller;

import com.hyperlofy.backend.platform.entity.BackupCatalog;
import com.hyperlofy.backend.platform.entity.PitrHistory;
import com.hyperlofy.backend.platform.entity.RestoreJob;
import com.hyperlofy.backend.platform.service.BackupAndRestoreService;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.tags.Tag;
import lombok.RequiredArgsConstructor;
import org.springframework.format.annotation.DateTimeFormat;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.*;

import java.time.ZonedDateTime;
import java.util.List;
import java.util.UUID;

@RestController
@RequestMapping("/api/v1/platform/backup")
@RequiredArgsConstructor
@Tag(name = "Platform Backup, Restore & PITR API", description = "Endpoints for triggering encrypted backups, executing automated restores, and point-in-time recovery (PITR)")
@PreAuthorize("hasAnyRole('ADMIN', 'SUPER_ADMIN')")
public class BackupAndRestoreController {

    private final BackupAndRestoreService backupService;

    @PostMapping("/trigger")
    @Operation(summary = "Trigger On-Demand Backup", description = "Executes an encrypted full or incremental backup for PostgreSQL, Redis, or Platform Config.")
    public ResponseEntity<BackupCatalog> triggerBackup(
            @RequestParam String targetSystem,
            @RequestParam String backupType,
            @RequestParam String storageLocation) {
        return ResponseEntity.ok(backupService.executeOnDemandBackup(targetSystem, backupType, storageLocation));
    }

    @PostMapping("/restore")
    @Operation(summary = "Initiate Restore Job", description = "Restores system state from a verified backup catalog snapshot.")
    public ResponseEntity<RestoreJob> restoreFromCatalog(@RequestParam UUID catalogId, @RequestParam String target) {
        return ResponseEntity.ok(backupService.initiateRestore(catalogId, target, "ADMIN_OPERATOR"));
    }

    @PostMapping("/pitr")
    @Operation(summary = "Execute Point-In-Time Recovery (PITR)", description = "Executes WAL replay to restore database to an exact target timestamp or LSN.")
    public ResponseEntity<PitrHistory> executePitr(
            @RequestParam @DateTimeFormat(iso = DateTimeFormat.ISO.DATE_TIME) ZonedDateTime targetTime,
            @RequestParam(required = false) String targetLsn) {
        return ResponseEntity.ok(backupService.executePitrRecovery(targetTime, targetLsn, "ADMIN_OPERATOR"));
    }

    @GetMapping("/catalog")
    @Operation(summary = "Get Verified Backup Catalog", description = "Lists active verified backup snapshots with SHA-256 checksums and retention metadata.")
    public ResponseEntity<List<BackupCatalog>> getVerifiedBackups() {
        return ResponseEntity.ok(backupService.getVerifiedBackups());
    }
}
