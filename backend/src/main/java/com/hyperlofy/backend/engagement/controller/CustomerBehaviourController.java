package com.hyperlofy.backend.engagement.controller;

import com.hyperlofy.backend.engagement.entity.CustomerBehaviourProfile;
import com.hyperlofy.backend.engagement.entity.CustomerSegment;
import com.hyperlofy.backend.engagement.service.AiCustomerEngagementService;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.tags.Tag;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.*;

import java.util.List;
import java.util.UUID;

@RestController
@RequestMapping("/api/v1/ai/customer")
@RequiredArgsConstructor
@Tag(name = "Customer Behaviour Intelligence API", description = "Query customer CLV, engagement scores, purchase frequency, preferred merchant categories, and AI-assigned segments")
@PreAuthorize("hasAnyRole('USER', 'MERCHANT', 'ADMIN', 'SUPER_ADMIN') or hasAuthority('SCOPE_internal')")
public class CustomerBehaviourController {

    private final AiCustomerEngagementService engagementService;

    @GetMapping("/{id}/profile")
    @Operation(summary = "Get Customer Behaviour Profile", description = "Returns customer engagement score, lifetime value (CLV), purchase frequency, and churn probability.")
    public ResponseEntity<CustomerBehaviourProfile> getProfile(@PathVariable UUID id) {
        return ResponseEntity.ok(engagementService.getBehaviourProfile(id));
    }

    @GetMapping("/{id}/segments")
    @Operation(summary = "Get Customer AI Segments", description = "Returns active AI customer segments (NEW, ACTIVE, LOYAL, VIP, HIGH_VALUE, PRICE_SENSITIVE, CHURN_RISK).")
    public ResponseEntity<List<CustomerSegment>> getSegments(@PathVariable UUID id) {
        return ResponseEntity.ok(engagementService.getCustomerSegments(id));
    }
}
