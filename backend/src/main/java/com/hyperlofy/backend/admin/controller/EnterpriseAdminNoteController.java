package com.hyperlofy.backend.admin.controller;

import com.hyperlofy.backend.admin.entity.AdminNote;
import com.hyperlofy.backend.common.service.EnterpriseAddendumService;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.tags.Tag;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.*;

import java.util.List;
import java.util.UUID;

@RestController
@RequestMapping("/api/v1/admin/enterprise")
@RequiredArgsConstructor
@Tag(name = "Enterprise Admin Management API", description = "Endpoints for admin internal notes, audit trail annotations, and onboarding checklists")
@PreAuthorize("hasAnyRole('ADMIN', 'SUPER_ADMIN')")
public class EnterpriseAdminNoteController {

    private final EnterpriseAddendumService addendumService;

    @PostMapping("/notes")
    @Operation(summary = "Add Admin Internal Note", description = "Attaches an internal admin note to a merchant, store, partner, product, or city entity.")
    public ResponseEntity<AdminNote> addNote(
            @RequestParam UUID targetId,
            @RequestParam String targetType,
            @RequestParam String content) {
        return ResponseEntity.ok(addendumService.addAdminNote(targetId, targetType, content, UUID.randomUUID()));
    }

    @GetMapping("/notes")
    @Operation(summary = "Get Target Entity Notes", description = "Fetches historical admin internal notes for a target entity.")
    public ResponseEntity<List<AdminNote>> getNotes(
            @RequestParam UUID targetId,
            @RequestParam String targetType) {
        return ResponseEntity.ok(addendumService.getAdminNotes(targetId, targetType));
    }
}
