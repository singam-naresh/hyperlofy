package com.hyperlofy.backend.engagement.controller;

import com.hyperlofy.backend.engagement.entity.PredictiveReorder;
import com.hyperlofy.backend.engagement.service.AiCustomerEngagementService;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.tags.Tag;
import lombok.RequiredArgsConstructor;
import org.springframework.format.annotation.DateTimeFormat;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.*;

import java.time.OffsetDateTime;
import java.util.List;
import java.util.UUID;

@RestController
@RequestMapping("/api/v1/reorders")
@RequiredArgsConstructor
@Tag(name = "Predictive Reorder Engine API", description = "Predict recurring purchases for groceries, milk, vegetables, and medicines with confidence scores and automated reminders")
@PreAuthorize("hasAnyRole('USER', 'MERCHANT', 'ADMIN', 'SUPER_ADMIN') or hasAuthority('SCOPE_internal')")
public class PredictiveReorderController {

    private final AiCustomerEngagementService engagementService;

    @GetMapping("/predictions")
    @Operation(summary = "Get Predictive Reorder Schedules", description = "Returns predicted recurring purchase schedules for groceries, milk, and household essentials.")
    public ResponseEntity<List<PredictiveReorder>> getPredictions(@RequestParam UUID customerId) {
        return ResponseEntity.ok(engagementService.getPredictiveReordersByCustomer(customerId));
    }

    @PostMapping
    @Operation(summary = "Schedule Predictive Reorder", description = "Schedules predictive reorder prediction with confidence score and reminder schedule.")
    public ResponseEntity<PredictiveReorder> schedule(
            @RequestParam String predictionCode,
            @RequestParam UUID customerId,
            @RequestParam UUID productId,
            @RequestParam(required = false) @DateTimeFormat(iso = DateTimeFormat.ISO.DATE_TIME) OffsetDateTime reorderDate) {
        return ResponseEntity.ok(engagementService.predictReorder(predictionCode, customerId, productId, reorderDate));
    }
}
