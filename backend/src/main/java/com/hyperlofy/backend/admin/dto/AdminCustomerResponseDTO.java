package com.hyperlofy.backend.admin.dto;

import io.swagger.v3.oas.annotations.media.Schema;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.math.BigDecimal;
import java.time.OffsetDateTime;
import java.util.UUID;

@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
@Schema(description = "Admin Customer Details View DTO")
public class AdminCustomerResponseDTO {

    @Schema(description = "Customer User ID")
    private UUID customerId;

    @Schema(description = "Full Name")
    private String fullName;

    @Schema(description = "Email Address")
    private String email;

    @Schema(description = "Phone Number")
    private String phoneNumber;

    @Schema(description = "Active Status")
    private boolean active;

    @Schema(description = "Total Lifetime Orders Placed")
    private Long totalOrdersCount;

    @Schema(description = "Wallet Balance")
    private BigDecimal walletBalance;

    @Schema(description = "Total Refunds Claimed")
    private Long refundCount;

    @Schema(description = "Account Created Timestamp")
    private OffsetDateTime createdAt;
}
