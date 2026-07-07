package com.hyperlofy.backend.analytics.dto;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.util.List;

@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class AgentPerformanceReport {
    private int onlineAgentsCount;
    private int offlineAgentsCount;
    private double averageDeliveryTimeMinutes;
    private List<String> topPerformers;
}
