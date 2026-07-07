package com.hyperlofy.backend.analytics.dto;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class CustomerRetentionReport {
    private int newUsersCount;
    private int returningUsersCount;
    private double userRetentionRatePercent;
}
