package com.hyperlofy.backend.order.dto;

import jakarta.validation.constraints.DecimalMax;
import jakarta.validation.constraints.DecimalMin;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import lombok.Builder;
import lombok.Data;

import java.util.UUID;

@Data
@Builder
public class OrderRequest {

    @NotNull(message = "Customer ID is required")
    private UUID customerId;

    @NotNull(message = "Zone ID is required")
    private UUID zoneId;

    @NotBlank(message = "Store Name is required")
    private String storeName;

    @NotNull(message = "Store Latitude is required")
    @DecimalMin(value = "-90.0")
    @DecimalMax(value = "90.0")
    private Double storeLatitude;

    @NotNull(message = "Store Longitude is required")
    @DecimalMin(value = "-180.0")
    @DecimalMax(value = "180.0")
    private Double storeLongitude;

    @NotBlank(message = "Delivery Address is required")
    private String deliveryAddress;

    @NotNull(message = "Delivery Latitude is required")
    @DecimalMin(value = "-90.0")
    @DecimalMax(value = "90.0")
    private Double deliveryLatitude;

    @NotNull(message = "Delivery Longitude is required")
    @DecimalMin(value = "-180.0")
    @DecimalMax(value = "180.0")
    private Double deliveryLongitude;

    @NotBlank(message = "Items description sequence is required")
    private String itemsDesc;

    private java.util.List<OrderItemDto> items;
}
