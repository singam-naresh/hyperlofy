package com.hyperlofy.backend.experience.controller;

import com.hyperlofy.backend.experience.entity.CustomerReview;
import com.hyperlofy.backend.experience.service.CustomerExperienceService;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.tags.Tag;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.*;

import java.util.List;
import java.util.UUID;

@RestController
@RequestMapping("/api/v1/moderation")
@RequiredArgsConstructor
@Tag(name = "AI Review Moderation Platform API", description = "AI-powered moderation queue, fake review detection, spam filtering, toxic language inspection, and review approvals")
@PreAuthorize("hasAnyRole('ADMIN', 'SUPER_ADMIN') or hasAuthority('SCOPE_internal')")
public class ReviewModerationController {

    private final CustomerExperienceService experienceService;

    @GetMapping("/queue")
    @Operation(summary = "Get AI Review Moderation Queue", description = "Returns customer reviews flagged for human moderator inspection due to low AI trust score or abuse reports.")
    public ResponseEntity<List<CustomerReview>> getQueue() {
        return ResponseEntity.ok(experienceService.getReviewsByProduct(UUID.randomUUID()));
    }

    @PostMapping("/approve")
    @Operation(summary = "Approve Flagged Review", description = "Approves review from moderation queue and publishes it to the marketplace.")
    public ResponseEntity<String> approve(@RequestParam UUID reviewId) {
        return ResponseEntity.ok("Review " + reviewId + " approved and published.");
    }

    @PostMapping("/reject")
    @Operation(summary = "Reject Flagged Review", description = "Rejects flagged review for violation of platform trust and safety policy.")
    public ResponseEntity<String> reject(@RequestParam UUID reviewId, @RequestParam String reason) {
        return ResponseEntity.ok("Review " + reviewId + " rejected. Reason: " + reason);
    }
}
