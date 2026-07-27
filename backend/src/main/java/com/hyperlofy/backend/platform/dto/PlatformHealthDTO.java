package com.hyperlofy.backend.platform.dto;

import io.swagger.v3.oas.annotations.media.Schema;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
@Schema(description = "Platform Health & Observability Metrics DTO")
public class PlatformHealthDTO {

    @Schema(description = "Database Health", example = "UP")
    private String databaseStatus;

    @Schema(description = "API Gateway Health", example = "UP")
    private String apiGatewayStatus;

    @Schema(description = "Redis Cache Health", example = "UP")
    private String cacheStatus;

    @Schema(description = "Message Queue Health", example = "UP")
    private String queueStatus;

    @Schema(description = "Scheduler Health", example = "UP")
    private String schedulerStatus;

    @Schema(description = "Application Version", example = "1.0.0-RELEASE")
    private String version;

    @Schema(description = "Uptime Duration in Seconds")
    private Long uptimeSeconds;

    @Schema(description = "Environment Name", example = "PRODUCTION")
    private String environment;

    @Schema(description = "JVM Memory Used (MB)")
    private Long jvmMemoryUsedMb;

    @Schema(description = "JVM Max Memory (MB)")
    private Long jvmMemoryMaxMb;
}
