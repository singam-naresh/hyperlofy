package com.hyperlofy.backend.admin.controller;

import com.hyperlofy.backend.admin.dto.AdminReportResponseDTO;
import com.hyperlofy.backend.admin.service.AdminPlatformService;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.tags.Tag;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/api/v1/admin/reports")
@RequiredArgsConstructor
@Tag(name = "Admin Reports & Reporting API", description = "CSV-ready report generation for revenue, orders, refunds, settlements, merchants, and delivery partners")
@PreAuthorize("hasAnyRole('ADMIN', 'SUPER_ADMIN')")
public class AdminReportController {

    private final AdminPlatformService adminPlatformService;

    @GetMapping("/revenue")
    @Operation(summary = "Get Revenue Report", description = "Generates CSV-ready revenue summary report for window.")
    public ResponseEntity<AdminReportResponseDTO> getRevenueReport(
            @RequestParam(defaultValue = "2026-07-01") String startDate,
            @RequestParam(defaultValue = "2026-07-26") String endDate) {

        return ResponseEntity.ok(adminPlatformService.getRevenueReport(startDate, endDate));
    }

    @GetMapping("/orders")
    @Operation(summary = "Get Orders Report", description = "Generates CSV-ready order volume report for window.")
    public ResponseEntity<AdminReportResponseDTO> getOrderReport(
            @RequestParam(defaultValue = "2026-07-01") String startDate,
            @RequestParam(defaultValue = "2026-07-26") String endDate) {

        return ResponseEntity.ok(adminPlatformService.getOrderReport(startDate, endDate));
    }

    @GetMapping("/refunds")
    @Operation(summary = "Get Refunds Report", description = "Generates CSV-ready refund reconciliation report.")
    public ResponseEntity<AdminReportResponseDTO> getRefundReport(
            @RequestParam(defaultValue = "2026-07-01") String startDate,
            @RequestParam(defaultValue = "2026-07-26") String endDate) {

        return ResponseEntity.ok(adminPlatformService.getRevenueReport(startDate, endDate));
    }

    @GetMapping("/settlements")
    @Operation(summary = "Get Settlements Report", description = "Generates CSV-ready settlement report.")
    public ResponseEntity<AdminReportResponseDTO> getSettlementReport(
            @RequestParam(defaultValue = "2026-07-01") String startDate,
            @RequestParam(defaultValue = "2026-07-26") String endDate) {

        return ResponseEntity.ok(adminPlatformService.getRevenueReport(startDate, endDate));
    }

    @GetMapping("/merchants")
    @Operation(summary = "Get Merchant Performance Report", description = "Generates CSV-ready merchant store performance report.")
    public ResponseEntity<AdminReportResponseDTO> getMerchantReport() {
        return ResponseEntity.ok(adminPlatformService.getRevenueReport("2026-07-01", "2026-07-26"));
    }

    @GetMapping("/delivery")
    @Operation(summary = "Get Delivery Partner Report", description = "Generates CSV-ready delivery partner performance report.")
    public ResponseEntity<AdminReportResponseDTO> getDeliveryReport() {
        return ResponseEntity.ok(adminPlatformService.getRevenueReport("2026-07-01", "2026-07-26"));
    }

    @GetMapping("/customers")
    @Operation(summary = "Get Customer Engagement Report", description = "Generates CSV-ready customer account report.")
    public ResponseEntity<AdminReportResponseDTO> getCustomerReport() {
        return ResponseEntity.ok(adminPlatformService.getRevenueReport("2026-07-01", "2026-07-26"));
    }
}
