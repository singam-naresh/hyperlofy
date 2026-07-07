package com.hyperlofy.backend.admin.dto;

import com.hyperlofy.backend.agent.entity.VerificationStatus;
import jakarta.validation.constraints.NotBlank;
import lombok.Builder;
import lombok.Data;

import java.time.OffsetDateTime;
import java.util.UUID;

public class AdminDto {

    @Data
    public static class ApproveAgentRequest {
        @NotBlank(message = "Remarks are mandatory for approving agents")
        private String remarks;
    }

    @Data
    public static class RejectAgentRequest {
        @NotBlank(message = "Rejection reason is mandatory")
        private String rejectionReason;
    }

    @Data
    public static class SuspendAgentRequest {
        @NotBlank(message = "Suspension reason is mandatory")
        private String suspensionReason;
    }

    @Data
    @Builder
    public static class SystemStatsResponse {
        private long totalUsersCount;
        private long customerCount;
        private long agentCount;
        private long pendingVerificationCount;
        private long approvedVerificationCount;
        private long rejectedVerificationCount;
        private long suspendedVerificationCount;
    }

    @Data
    @Builder
    public static class VerificationLogResponse {
        private UUID logId;
        private UUID agentId;
        private String adminEmail;
        private VerificationStatus previousStatus;
        private VerificationStatus newStatus;
        private String remarks;
        private OffsetDateTime timestamp;
    }
}
