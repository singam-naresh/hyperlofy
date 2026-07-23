package com.hyperlofy.backend.ai.merchantselection;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.util.ArrayList;
import java.util.List;
import java.util.UUID;

@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class MerchantSelectionPlan {
    private UUID planId;
    private UUID draftId;
    private String selectionType;
    private List<MerchantCandidate> candidateMerchants = new ArrayList<>();
    private List<MerchantCandidate> selectedMerchants = new ArrayList<>();
    private double selectionScore;
    private List<MerchantReason> reasoningCodes = new ArrayList<>();
    private List<String> warnings = new ArrayList<>();
    private String fallbackStrategy;
    private SelectionSummary summary;
}
