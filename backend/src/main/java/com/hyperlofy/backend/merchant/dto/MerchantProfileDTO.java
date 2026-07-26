package com.hyperlofy.backend.merchant.dto;

import io.swagger.v3.oas.annotations.media.Schema;
import jakarta.validation.constraints.NotBlank;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.math.BigDecimal;
import java.util.UUID;

@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
@Schema(description = "Merchant Business Profile DTO")
public class MerchantProfileDTO {

    @Schema(description = "Merchant ID")
    private UUID merchantId;

    @NotBlank(message = "Business name cannot be blank")
    @Schema(description = "Business Name", example = "Organic Fresh Mart")
    private String businessName;

    @Schema(description = "Contact Email", example = "contact@freshmart.com")
    private String contactEmail;

    @Schema(description = "Contact Phone", example = "+919876543210")
    private String contactPhone;

    @Schema(description = "Store Operating Timings", example = "09:00 AM - 10:00 PM")
    private String storeTimings;

    @Schema(description = "Profile Image URL", example = "https://images.hyperlofy.com/merchant/freshmart.png")
    private String profileImageUrl;

    @Schema(description = "Bank Account Holder Name", example = "Organic Fresh Mart Pvt Ltd")
    private String bankHolderName;

    @Schema(description = "Bank Account Number", example = "918237465012")
    private String bankAccountNumber;

    @Schema(description = "Bank IFSC Code", example = "HDFC0001234")
    private String bankIfscCode;

    @Schema(description = "Merchant Rating")
    private BigDecimal rating;
}
