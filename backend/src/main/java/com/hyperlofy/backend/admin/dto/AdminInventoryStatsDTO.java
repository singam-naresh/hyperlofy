package com.hyperlofy.backend.admin.dto;

import com.hyperlofy.backend.inventory.entity.Inventory;
import io.swagger.v3.oas.annotations.media.Schema;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.util.List;

@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
@Schema(description = "Admin Inventory Monitoring Statistics DTO")
public class AdminInventoryStatsDTO {

    @Schema(description = "Total Inventory Items Count")
    private Long totalItemsCount;

    @Schema(description = "In-Stock Items Count")
    private Long inStockCount;

    @Schema(description = "Low Stock Items Count")
    private Long lowStockCount;

    @Schema(description = "Out-Of-Stock Items Count")
    private Long outOfStockCount;

    @Schema(description = "Low Stock Items List")
    private List<Inventory> lowStockItems;

    @Schema(description = "Out of Stock Items List")
    private List<Inventory> outOfStockItems;
}
