package com.hyperlofy.backend.agent.entity;

/**
 * State machine stages for hyperlocal Agent vetting.
 */
public enum VerificationStatus {
    PENDING,
    APPROVED,
    REJECTED,
    SUSPENDED
}
