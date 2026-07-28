package com.hyperlofy.backend.common.exception;

import lombok.Getter;
import org.springframework.http.HttpStatus;

@Getter
public enum GlobalErrorCode {

    // Auth & Security
    AUTH_001("AUTH_001", HttpStatus.UNAUTHORIZED, "Invalid Credentials", "Authentication failed due to incorrect username, password, or token."),
    AUTH_002("AUTH_002", HttpStatus.UNAUTHORIZED, "OTP Expired", "One-Time Password has expired or is invalid."),
    AUTH_003("AUTH_003", HttpStatus.UNAUTHORIZED, "Session Expired", "JWT session has expired. Please authenticate again."),
    AUTH_004("AUTH_004", HttpStatus.FORBIDDEN, "Account Locked", "Account has been locked due to excessive failed attempts."),
    AUTH_005("AUTH_005", HttpStatus.FORBIDDEN, "Access Denied", "Insufficient permissions for requested resource scope."),

    // Merchant & Store
    MERCHANT_001("MERCHANT_001", HttpStatus.BAD_REQUEST, "Store Closed", "Selected store is currently closed or suspended."),
    MERCHANT_002("MERCHANT_002", HttpStatus.FORBIDDEN, "Merchant Suspended", "Merchant account is under review or suspended."),

    // Marketplace & Inventory
    MARKETPLACE_001("MARKETPLACE_001", HttpStatus.NOT_FOUND, "Product Not Found", "Product or variant SKU does not exist."),
    MARKETPLACE_002("MARKETPLACE_002", HttpStatus.CONFLICT, "Inventory Not Available", "Requested product quantity exceeds available stock."),

    // Commerce & Orders
    ORDER_001("ORDER_001", HttpStatus.BAD_REQUEST, "Cart Empty", "Cannot place an order with an empty shopping cart."),
    ORDER_002("ORDER_002", HttpStatus.BAD_REQUEST, "Store Mismatch", "Cart items must belong to a single store location."),
    ORDER_003("ORDER_003", HttpStatus.BAD_REQUEST, "Order Cancelled", "Order state transition invalid or order was cancelled."),

    // Payments & Wallet
    PAYMENT_001("PAYMENT_001", HttpStatus.PAYMENT_REQUIRED, "Payment Failed", "Payment processing gateway returned failure."),
    PAYMENT_002("PAYMENT_002", HttpStatus.REQUEST_TIMEOUT, "Payment Timeout", "Payment gateway session timed out."),
    PAYMENT_003("PAYMENT_003", HttpStatus.CONFLICT, "Duplicate Payment", "Payment idempotency key already processed."),

    // Tracking & Driver
    TRACKING_001("TRACKING_001", HttpStatus.NOT_FOUND, "Driver Offline", "No available delivery partner found in dispatch zone."),
    TRACKING_002("TRACKING_002", HttpStatus.SERVICE_UNAVAILABLE, "Location Unavailable", "GPS location stream offline."),

    // Notifications
    NOTIFICATION_001("NOTIFICATION_001", HttpStatus.INTERNAL_SERVER_ERROR, "SMS Failed", "SMS dispatch provider encountered error."),
    NOTIFICATION_002("NOTIFICATION_002", HttpStatus.INTERNAL_SERVER_ERROR, "Email Failed", "Email SMTP gateway connection failed."),

    // Infrastructure & System
    SYSTEM_001("SYSTEM_001", HttpStatus.INTERNAL_SERVER_ERROR, "Internal Server Error", "An unhandled system exception occurred."),
    SYSTEM_002("SYSTEM_002", HttpStatus.INTERNAL_SERVER_ERROR, "Database Error", "Relational database transaction failed."),
    SYSTEM_003("SYSTEM_003", HttpStatus.SERVICE_UNAVAILABLE, "Redis Unavailable", "In-memory cache cluster disconnected."),
    SYSTEM_004("SYSTEM_004", HttpStatus.INTERNAL_SERVER_ERROR, "Configuration Missing", "Required environment property missing."),

    // Input Validation & Rate Limiting
    VALIDATION_001("VALIDATION_001", HttpStatus.BAD_REQUEST, "Invalid Input", "Request body failed bean validation constraints."),
    VALIDATION_002("VALIDATION_002", HttpStatus.BAD_REQUEST, "Missing Required Field", "Required header or query parameter missing."),
    RATE_LIMIT_001("RATE_LIMIT_001", HttpStatus.TOO_MANY_REQUESTS, "Too Many Requests", "API rate limit bucket exhausted. Retry later."),
    SECURITY_001("SECURITY_001", HttpStatus.UNAUTHORIZED, "Invalid JWT", "JWT signature or bearer header malformed."),
    SECURITY_002("SECURITY_002", HttpStatus.FORBIDDEN, "CSRF Detected", "CSRF token validation failed."),
    SECURITY_003("SECURITY_003", HttpStatus.FORBIDDEN, "Suspicious Request", "Malicious payload signature detected.");

    private final String errorCode;
    private final HttpStatus httpStatus;
    private final String message;
    private final String developerMessage;

    GlobalErrorCode(String errorCode, HttpStatus httpStatus, String message, String developerMessage) {
        this.errorCode = errorCode;
        this.httpStatus = httpStatus;
        this.message = message;
        this.developerMessage = developerMessage;
    }
}
