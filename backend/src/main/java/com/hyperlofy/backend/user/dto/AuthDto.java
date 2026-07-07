package com.hyperlofy.backend.user.dto;

import com.hyperlofy.backend.user.entity.Role;
import jakarta.validation.constraints.Email;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Pattern;
import jakarta.validation.constraints.Size;
import lombok.Builder;
import lombok.Data;

/**
 * Container of DTOs for the Authorization context limit.
 */
public class AuthDto {

    @Data
    public static class RegisterRequest {
        @NotBlank(message = "Email is mandatory")
        @Email(message = "Provide a valid email format")
        private String email;

        @NotBlank(message = "Password is mandatory")
        @Size(min = 8, message = "Password must be at least 8 characters long")
        private String password;

        @NotBlank(message = "First name is mandatory")
        private String firstName;

        @NotBlank(message = "Last name is mandatory")
        private String lastName;

        @NotBlank(message = "Phone number is mandatory")
        @Pattern(regexp = "^[0-9]{10,15}$", message = "Phone must contain 10-15 digits")
        private String phoneNumber;

        @NotNull(message = "Role specification is mandatory")
        private Role role; // CUSTOMER, AGENT, ADMIN, SUPER_ADMIN

        // Optional parameters for nested profiles depending on selected role
        private String defaultDeliveryAddress;
        private Double gpsLatitude;
        private Double gpsLongitude;

        // Mandatories for Agent onboarding
        private String vehicleType;
        private String vehicleNumber;
        private String panNumber;
        private String aadhaarNumber;
    }

    @Data
    public static class LoginRequest {
        @NotBlank(message = "Email is mandatory")
        @Email(message = "Provide a valid email")
        private String email;

        @NotBlank(message = "Password is mandatory")
        private String password;
    }

    @Data
    public static class RefreshRequest {
        @NotBlank(message = "Refresh token is mandatory")
        private String refreshToken;
    }

    @Data
    @Builder
    public static class TokenResponse {
        private String accessToken;
        private String refreshToken;
        private String tokenType;
        private String userId;
        private String email;
        private Role role;
    }

    @Data
    public static class PasswordResetRequest {
        @NotBlank(message = "Email is mandatory")
        @Email(message = "Provide a valid email")
        private String email;

        @NotBlank(message = "New password is mandatory")
        @Size(min = 8, message = "Password must be at least 8 characters long")
        private String newPassword;
    }
}
