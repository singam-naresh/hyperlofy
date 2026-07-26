package com.hyperlofy.backend.delivery.dto;

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
@Schema(description = "Delivery Partner Profile DTO")
public class DeliveryProfileDTO {

    @Schema(description = "Agent User ID")
    private UUID agentId;

    @NotBlank(message = "Full name is required")
    @Schema(description = "Full Name", example = "Ramesh Kumar")
    private String fullName;

    @Schema(description = "Phone Number", example = "+919876543210")
    private String phoneNumber;

    @Schema(description = "Email Address", example = "ramesh@hyperlofy.com")
    private String email;

    @Schema(description = "Profile Photo URL", example = "https://images.hyperlofy.com/agents/ramesh.jpg")
    private String profilePhoto;

    @Schema(description = "Vehicle Type (BIKE, SCOOTER, EV_BIKE)", example = "BIKE")
    private String vehicleType;

    @Schema(description = "Vehicle License Plate Number", example = "AP 03 AB 1234")
    private String vehicleNumber;

    @Schema(description = "Driving Licence Number", example = "DL-1420110012345")
    private String drivingLicence;

    @Schema(description = "Emergency Contact Number", example = "+919876543211")
    private String emergencyContact;

    @Schema(description = "Bank Account Holder Name", example = "Ramesh Kumar")
    private String bankHolderName;

    @Schema(description = "Bank Account Number", example = "918237465012")
    private String bankAccountNumber;

    @Schema(description = "Bank IFSC Code", example = "HDFC0001234")
    private String bankIfscCode;

    @Schema(description = "Delivery Partner Rating")
    private BigDecimal rating;
}
