package com.hyperlofy.backend.customer.dto;

import io.swagger.v3.oas.annotations.media.Schema;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

import java.util.UUID;

@Getter
@Setter
@Builder
@NoArgsConstructor
@AllArgsConstructor
@Schema(description = "Customer Profile Management DTO")
public class CustomerProfileDTO {

    @Schema(description = "User ID")
    private UUID userId;

    @Schema(description = "First Name")
    private String firstName;

    @Schema(description = "Last Name")
    private String lastName;

    @Schema(description = "Email Address")
    private String email;

    @Schema(description = "Phone Number")
    private String phoneNumber;

    @Schema(description = "Preferred Language", example = "EN")
    private String preferredLanguage;

    @Schema(description = "Push Notifications Enabled Flag")
    private boolean pushNotificationsEnabled;
}
