package com.hyperlofy.backend.agent.dto;

import com.hyperlofy.backend.agent.entity.VerificationStatus;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import lombok.Builder;
import lombok.Data;

import java.time.OffsetDateTime;
import java.util.UUID;

public class AgentDto {

    @Data
    @Builder
    public static class ProfileResponse {
        private UUID profileId;
        private UUID userId;
        private String email;
        private String firstName;
        private String lastName;
        private String phoneNumber;
        
        private String vehicleType;
        private String vehicleNumber;
        private Double currentGpsLatitude;
        private Double currentGpsLongitude;
        private boolean isAvailable;
        
        // Verification & Compliance details
        private String panNumber;
        private String panDocUrl;
        private String aadhaarNumber;
        private String aadhaarDocUrl;
        private String profileImageUrl;
        
        private VerificationStatus verificationStatus;
        private String rejectionReason;
        private OffsetDateTime suspendedAt;
        private String suspensionReason;
    }

    @Data
    public static class UpdateAvailabilityRequest {
        @NotNull(message = "Availability status is mandatory")
        private Boolean available;
    }

    @Data
    public static class LocationUpdateRequest {
        @NotNull(message = "Lattitude is required")
        private Double latitude;

        @NotNull(message = "Longitude is required")
        private Double longitude;
    }

    @Data
    public static class UploadDocumentsRequest {
        @NotBlank(message = "PAN documentation reference url is mandatory")
        private String panDocUrl;

        @NotBlank(message = "Aadhaar documentation reference url is mandatory")
        private String aadhaarDocUrl;

        @NotBlank(message = "Profile image reference url is mandatory")
        private String profileImageUrl;
    }
}
