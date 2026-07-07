package com.hyperlofy.backend.customer.dto;

import lombok.Builder;
import lombok.Data;

import java.util.UUID;

public class CustomerDto {

    @Data
    @Builder
    public static class ProfileResponse {
        private UUID profileId;
        private UUID userId;
        private String email;
        private String firstName;
        private String lastName;
        private String phoneNumber;
        private String defaultDeliveryAddress;
        private Double gpsLatitude;
        private Double gpsLongitude;
        private String preferredPaymentMethod;
    }

    @Data
    public static class UpdateProfileRequest {
        private String defaultDeliveryAddress;
        private Double gpsLatitude;
        private Double gpsLongitude;
        private String preferredPaymentMethod;
    }
}
