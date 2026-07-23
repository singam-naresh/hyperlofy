package com.hyperlofy.backend.ai.merchantselection;

import com.hyperlofy.backend.ai.memory.dto.MemoryDto;
import com.hyperlofy.backend.ai.merchantselection.MerchantSelectionMemoryEnhancer;
import com.hyperlofy.backend.ai.orderbuilder.OrderDraft;
import com.hyperlofy.backend.zone.entity.Zone;
import com.hyperlofy.backend.zone.repository.ZoneRepository;
import com.hyperlofy.backend.zone.service.GeoLocationService;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;

import java.util.ArrayList;
import java.util.Comparator;
import java.util.List;
import java.util.UUID;

@Slf4j
@Service
@RequiredArgsConstructor
public class MerchantSelectionService {

    private static final SelectionWeights DEFAULT_WEIGHTS = SelectionWeights.builder()
            .distanceWeight(0.45)
            .availabilityWeight(0.15)
            .zoneWeight(0.15)
            .capabilityWeight(0.10)
            .inventoryWeight(0.15)
            .build();

    private final ZoneRepository zoneRepository;
    private final GeoLocationService geoLocationService;
    private final MerchantSelectionFactory merchantSelectionFactory;
    private final MerchantSelectionMemoryEnhancer memoryEnhancer;

    public MerchantSelectionResponse select(MerchantSelectionRequest request) {
        long startedAt = System.currentTimeMillis();
        OrderDraft draft = request.getDraft();
        if (draft == null || draft.getDraftId() == null || draft.getConversationId() == null || draft.getCustomerId() == null) {
            return MerchantSelectionResponse.builder()
                    .success(false)
                    .message("Invalid draft")
                    .build();
        }

        if (request.getLatitude() == null || request.getLongitude() == null) {
            return MerchantSelectionResponse.builder()
                    .success(false)
                    .message("Invalid coordinates")
                    .build();
        }

        try {
            memoryEnhancer.applyMemoryBias(request);
            List<MerchantCandidate> candidates = buildMerchantCandidates(request);
            if (candidates.isEmpty()) {
                MerchantSelectionPlan plan = MerchantSelectionPlan.builder()
                        .planId(UUID.randomUUID())
                        .draftId(draft.getDraftId())
                        .selectionType("NO_MATCH")
                        .candidateMerchants(new ArrayList<>())
                        .selectedMerchants(new ArrayList<>())
                        .selectionScore(0.0)
                        .reasoningCodes(List.of(MerchantReason.builder().code("NO_COVERAGE").message("No eligible merchants found").build()))
                        .warnings(List.of("No merchant coverage for the provided location"))
                        .fallbackStrategy("MANUAL_REVIEW")
                        .summary(SelectionSummary.builder()
                                .candidateCount(0)
                                .selectedCount(0)
                                .selectionType("NO_MATCH")
                                .averageScore(0.0)
                                .build())
                        .build();

                return MerchantSelectionResponse.builder()
                        .success(false)
                        .plan(plan)
                        .message("No merchant found")
                        .build();
            }

            MerchantRankingStrategy rankingStrategy = merchantSelectionFactory.resolve(draft.getIntent());
            List<MerchantCandidate> ranked = rankingStrategy.rank(candidates, request);
            MerchantCandidate selected = ranked.stream().findFirst().orElse(null);

            double averageScore = ranked.stream().mapToDouble(MerchantCandidate::getScore).average().orElse(0.0);
            MerchantSelectionPlan plan = MerchantSelectionPlan.builder()
                    .planId(UUID.randomUUID())
                    .draftId(draft.getDraftId())
                    .selectionType(selected != null ? "SINGLE_STORE" : "NO_MATCH")
                    .candidateMerchants(ranked)
                    .selectedMerchants(selected == null ? new ArrayList<>() : List.of(selected))
                    .selectionScore(selected != null ? selected.getScore() : 0.0)
                    .reasoningCodes(List.of(MerchantReason.builder().code("BEST_SCORE").message("Highest ranked eligible merchant selected").build()))
                    .warnings(new ArrayList<>())
                    .fallbackStrategy("MANUAL_REVIEW")
                    .summary(SelectionSummary.builder()
                            .candidateCount(candidates.size())
                            .selectedCount(selected == null ? 0 : 1)
                            .selectionType(selected != null ? "SINGLE_STORE" : "NO_MATCH")
                            .averageScore(averageScore)
                            .build())
                    .build();

            long duration = System.currentTimeMillis() - startedAt;
            log.info("Merchant selection completed. DraftId={}, SelectionId={}, MerchantCount={}, SelectionType={}, ExecutionTime={}ms, Success=true",
                    draft.getDraftId(), plan.getPlanId(), candidates.size(), plan.getSelectionType(), duration);
            return MerchantSelectionResponse.builder()
                    .success(true)
                    .plan(plan)
                    .message("Merchant selection plan created")
                    .build();
        } catch (Exception ex) {
            long duration = System.currentTimeMillis() - startedAt;
            log.warn("Merchant selection failed. DraftId={}, ExecutionTime={}ms, Success=false", draft.getDraftId(), duration, ex);
            return MerchantSelectionResponse.builder()
                    .success(false)
                    .message("Selection failure")
                    .build();
        }
    }

    private List<MerchantCandidate> buildMerchantCandidates(MerchantSelectionRequest request) {
        List<Zone> zones = zoneRepository.findByActiveTrue();
        List<MerchantCandidate> candidates = new ArrayList<>();

        for (Zone zone : zones) {
            boolean within = geoLocationService.isWithinRadius(
                    request.getLatitude(),
                    request.getLongitude(),
                    zone.getCenterLatitude(),
                    zone.getCenterLongitude(),
                    zone.getRadiusKm()
            );
            if (!within) {
                continue;
            }

            double distance = geoLocationService.calculateDistanceKm(
                    request.getLatitude(),
                    request.getLongitude(),
                    zone.getCenterLatitude(),
                    zone.getCenterLongitude()
            );

            MerchantCandidate candidate = MerchantCandidate.builder()
                    .merchantId(zone.getId())
                    .merchantName(zone.getName())
                    .zoneName(zone.getName())
                    .latitude(zone.getCenterLatitude())
                    .longitude(zone.getCenterLongitude())
                    .distanceKm(distance)
                    .available(true)
                    .active(zone.isActive())
                    .coveredByZone(true)
                    .capabilities(List.of("ZONE_COVERAGE"))
                    .inventoryStatus("UNKNOWN")
                    .build();

            double baseScore = calculateCompositeScore(candidate, request);
            List<MemoryDto> memoryList = List.of();
            if (request.getCustomerPreferences() != null) {
                Object memories = request.getCustomerPreferences().get("memories");
                if (memories instanceof List<?> castList) {
                    memoryList = castList.stream()
                            .filter(MemoryDto.class::isInstance)
                            .map(MemoryDto.class::cast)
                            .toList();
                }
            }
            double memoryBoost = memoryEnhancer.boostMerchantScore(candidate, request.getDraft(), memoryList);
            candidate.setScore(Math.min(1.0, baseScore + memoryBoost));
            candidates.add(candidate);
        }

        return candidates.stream()
                .sorted(Comparator.comparingDouble(MerchantCandidate::getScore).reversed())
                .toList();
    }

    private double calculateCompositeScore(MerchantCandidate candidate, MerchantSelectionRequest request) {
        double distanceScore = 1.0 / (1.0 + candidate.getDistanceKm());
        double availabilityScore = candidate.isAvailable() ? 1.0 : 0.0;
        double zoneScore = candidate.isCoveredByZone() ? 1.0 : 0.0;
        double capabilityScore = candidate.getCapabilities() != null && !candidate.getCapabilities().isEmpty() ? 1.0 : 0.5;
        double inventoryScore = "UNKNOWN".equalsIgnoreCase(candidate.getInventoryStatus()) ? 0.5 : 1.0;

        return (DEFAULT_WEIGHTS.getDistanceWeight() * distanceScore)
                + (DEFAULT_WEIGHTS.getAvailabilityWeight() * availabilityScore)
                + (DEFAULT_WEIGHTS.getZoneWeight() * zoneScore)
                + (DEFAULT_WEIGHTS.getCapabilityWeight() * capabilityScore)
                + (DEFAULT_WEIGHTS.getInventoryWeight() * inventoryScore);
    }
}
