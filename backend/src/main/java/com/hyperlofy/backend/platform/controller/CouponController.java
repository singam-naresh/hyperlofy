package com.hyperlofy.backend.platform.controller;

import com.hyperlofy.backend.platform.dto.CouponValidationResultDTO;
import com.hyperlofy.backend.platform.entity.Coupon;
import com.hyperlofy.backend.platform.service.PlatformAdministrationService;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.tags.Tag;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.*;

import java.math.BigDecimal;
import java.util.List;
import java.util.UUID;

@RestController
@RequestMapping("/api/v1/coupons")
@RequiredArgsConstructor
@Tag(name = "Platform Coupon Engine API", description = "Endpoints for coupon management, discount validation, preview, and activation")
public class CouponController {

    private final PlatformAdministrationService platformService;

    @GetMapping
    @PreAuthorize("hasAnyRole('ADMIN', 'SUPER_ADMIN', 'MERCHANT')")
    @Operation(summary = "List Coupons", description = "Retrieves configured discount coupons.")
    public ResponseEntity<List<Coupon>> getAllCoupons() {
        return ResponseEntity.ok(platformService.getAllCoupons());
    }

    @PostMapping
    @PreAuthorize("hasAnyRole('ADMIN', 'SUPER_ADMIN')")
    @Operation(summary = "Create Coupon", description = "Creates a new promotional or store coupon.")
    public ResponseEntity<Coupon> createCoupon(@Valid @RequestBody Coupon coupon) {
        return ResponseEntity.ok(platformService.createCoupon(coupon));
    }

    @PatchMapping("/{id}/activate")
    @PreAuthorize("hasAnyRole('ADMIN', 'SUPER_ADMIN')")
    @Operation(summary = "Activate/Deactivate Coupon", description = "Toggles coupon active status.")
    public ResponseEntity<Coupon> setCouponActive(@PathVariable UUID id, @RequestParam boolean active) {
        return ResponseEntity.ok(platformService.setCouponActive(id, active));
    }

    @PostMapping("/validate")
    @Operation(summary = "Validate Coupon Code", description = "Validates a coupon code and calculates discount preview.")
    public ResponseEntity<CouponValidationResultDTO> validateCoupon(
            @RequestParam String code,
            @RequestParam BigDecimal orderSubtotal) {

        return ResponseEntity.ok(platformService.validateCoupon(code, orderSubtotal));
    }
}
