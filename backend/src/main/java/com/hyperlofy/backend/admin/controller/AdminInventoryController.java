package com.hyperlofy.backend.admin.controller;

import com.hyperlofy.backend.admin.dto.AdminInventoryStatsDTO;
import com.hyperlofy.backend.admin.service.AdminPlatformService;
import com.hyperlofy.backend.inventory.entity.Inventory;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.tags.Tag;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.Page;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/api/v1/admin/inventory")
@RequiredArgsConstructor
@Tag(name = "Admin Inventory Monitoring API", description = "Endpoints for platform-wide stock monitoring, low-stock alerts, and inventory health")
@PreAuthorize("hasAnyRole('ADMIN', 'SUPER_ADMIN')")
public class AdminInventoryController {

    private final AdminPlatformService adminPlatformService;

    @GetMapping
    @Operation(summary = "List Platform Inventory", description = "Paginated list of store inventory items across all merchants.")
    public ResponseEntity<Page<Inventory>> getInventory(
            @RequestParam(defaultValue = "0") int page,
            @RequestParam(defaultValue = "20") int size) {

        return ResponseEntity.ok(adminPlatformService.getInventory(page, size));
    }

    @GetMapping("/low-stock")
    @Operation(summary = "Get Low Stock Items", description = "Retrieves inventory items below low stock thresholds.")
    public ResponseEntity<List<Inventory>> getLowStockInventory() {
        return ResponseEntity.ok(adminPlatformService.getLowStockInventory());
    }

    @GetMapping("/out-of-stock")
    @Operation(summary = "Get Out of Stock Items", description = "Retrieves items with zero available stock.")
    public ResponseEntity<List<Inventory>> getOutOfStockInventory() {
        return ResponseEntity.ok(adminPlatformService.getOutOfStockInventory());
    }

    @GetMapping("/statistics")
    @Operation(summary = "Get Inventory Statistics", description = "Retrieves consolidated platform inventory stats.")
    public ResponseEntity<AdminInventoryStatsDTO> getInventoryStats() {
        return ResponseEntity.ok(adminPlatformService.getInventoryStats());
    }
}
