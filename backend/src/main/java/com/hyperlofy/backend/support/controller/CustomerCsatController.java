package com.hyperlofy.backend.support.controller;

import com.hyperlofy.backend.support.entity.CustomerCsatSurvey;
import com.hyperlofy.backend.support.service.CustomerSupportService;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.tags.Tag;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.*;

import java.util.UUID;

@RestController
@RequestMapping("/api/v1/support/csat")
@RequiredArgsConstructor
@Tag(name = "Customer Satisfaction (CSAT/NPS/CES) API", description = "Submit post-ticket customer satisfaction (CSAT 1-5), Net Promoter Score (NPS 0-10), and Effort Scores (CES 1-7)")
@PreAuthorize("hasAnyRole('USER', 'SUPPORT', 'AGENT', 'ADMIN', 'SUPER_ADMIN') or hasAuthority('SCOPE_internal')")
public class CustomerCsatController {

    private final CustomerSupportService supportService;

    @PostMapping
    @Operation(summary = "Submit Post-Support CSAT / NPS Survey", description = "Records CSAT rating (1-5), Net Promoter Score (0-10), Customer Effort Score (1-7), and text feedback.")
    public ResponseEntity<CustomerCsatSurvey> submitSurvey(
            @RequestParam UUID ticketId,
            @RequestParam UUID customerId,
            @RequestParam(required = false) Integer csat,
            @RequestParam(required = false) Integer nps,
            @RequestParam(required = false) Integer ces,
            @RequestParam(required = false) String feedback) {
        return ResponseEntity.ok(supportService.submitSurvey(ticketId, customerId, csat, nps, ces, feedback));
    }
}
