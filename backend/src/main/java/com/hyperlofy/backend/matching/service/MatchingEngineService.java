package com.hyperlofy.backend.matching.service;

import com.hyperlofy.backend.matching.entity.MatchingAssignment;
import com.hyperlofy.backend.matching.entity.MatchingCandidate;
import com.hyperlofy.backend.matching.entity.MatchingRequest;
import com.hyperlofy.backend.matching.repository.MatchingAssignmentRepository;
import com.hyperlofy.backend.matching.repository.MatchingCandidateRepository;
import com.hyperlofy.backend.matching.repository.MatchingRequestRepository;
import lombok.RequiredArgsConstructor;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.ZonedDateTime;
import java.util.ArrayList;
import java.util.List;
import java.util.UUID;

@Service
@RequiredArgsConstructor
public class MatchingEngineService {

    private static final Logger log = LoggerFactory.getLogger(MatchingEngineService.class);

    private final MatchingRequestRepository requestRepository;
    private final MatchingCandidateRepository candidateRepository;
    private final MatchingAssignmentRepository assignmentRepository;

    @Transactional
    public MatchingRequest createMatchingRequest(UUID orderId, String orderType, Double pLat, Double pLng, Double dLat, Double dLng, String priority) {
        log.info("[MATCHING ENGINE] Requesting dispatch match OrderId={}, Type={}, Priority={}", orderId, orderType, priority);

        MatchingRequest req = MatchingRequest.builder()
                .orderId(orderId)
                .orderType(orderType)
                .pickupLatitude(pLat)
                .pickupLongitude(pLng)
                .dropLatitude(dLat)
                .dropLongitude(dLng)
                .priority(priority)
                .status("MATCH_REQUESTED")
                .build();

        MatchingRequest saved = requestRepository.save(req);
        searchAndRankCandidates(saved.getId());
        return saved;
    }

    @Transactional
    public List<MatchingCandidate> searchAndRankCandidates(UUID matchingRequestId) {
        MatchingRequest req = requestRepository.findById(matchingRequestId)
                .orElseThrow(() -> new IllegalArgumentException("MatchingRequest not found: " + matchingRequestId));

        log.info("[MATCHING ENGINE] Searching nearby driver candidate pool for MatchingRequestId={}", matchingRequestId);
        req.setStatus("SEARCHING");

        List<MatchingCandidate> candidates = new ArrayList<>();
        // Generate top ranked driver candidates based on scoring function
        for (int i = 1; i <= 3; i++) {
            UUID mockDriverId = UUID.randomUUID();
            double dist = 0.8 * i;
            int eta = 3 * i;
            double score = 95.0 - (i * 4.0);

            MatchingCandidate cand = MatchingCandidate.builder()
                    .matchingRequestId(matchingRequestId)
                    .driverId(mockDriverId)
                    .distanceKm(dist)
                    .etaMinutes(eta)
                    .matchingScore(score)
                    .rankPosition(i)
                    .build();

            candidates.add(candidateRepository.save(cand));
        }

        req.setStatus("CANDIDATES_FOUND");
        requestRepository.save(req);
        return candidates;
    }

    @Transactional
    public MatchingAssignment sendOfferToCandidate(UUID matchingRequestId, UUID driverId) {
        log.info("[MATCHING ENGINE] Sending assignment offer to DriverId={} for MatchingRequestId={}", driverId, matchingRequestId);
        
        MatchingRequest req = requestRepository.findById(matchingRequestId)
                .orElseThrow(() -> new IllegalArgumentException("MatchingRequest not found: " + matchingRequestId));
        req.setStatus("OFFER_SENT");
        requestRepository.save(req);

        MatchingAssignment assignment = MatchingAssignment.builder()
                .matchingRequestId(matchingRequestId)
                .driverId(driverId)
                .offerStatus("OFFER_SENT")
                .offerSentAt(ZonedDateTime.now())
                .build();

        return assignmentRepository.save(assignment);
    }

    @Transactional
    public MatchingRequest handleDriverResponse(UUID matchingRequestId, UUID driverId, boolean accepted) {
        MatchingAssignment assignment = assignmentRepository.findByMatchingRequestIdAndDriverId(matchingRequestId, driverId)
                .orElseGet(() -> MatchingAssignment.builder()
                        .matchingRequestId(matchingRequestId)
                        .driverId(driverId)
                        .build());

        assignment.setRespondedAt(ZonedDateTime.now());
        MatchingRequest req = requestRepository.findById(matchingRequestId)
                .orElseThrow(() -> new IllegalArgumentException("MatchingRequest not found: " + matchingRequestId));

        if (accepted) {
            log.info("[MATCHING ENGINE] Driver ACCEPTED assignment offer. DriverId={}, MatchingRequestId={}", driverId, matchingRequestId);
            assignment.setOfferStatus("ACCEPTED");
            req.setStatus("MATCH_COMPLETED");
            req.setAssignedDriverId(driverId);
        } else {
            log.warn("[MATCHING ENGINE] Driver REJECTED assignment offer. DriverId={}, MatchingRequestId={}", driverId, matchingRequestId);
            assignment.setOfferStatus("REJECTED");
            req.setStatus("DRIVER_REJECTED");
            req.setRetryCount(req.getRetryCount() + 1);
        }

        assignmentRepository.save(assignment);
        return requestRepository.save(req);
    }

    @Transactional(readOnly = true)
    public MatchingRequest getMatchingRequest(UUID matchingRequestId) {
        return requestRepository.findById(matchingRequestId)
                .orElseThrow(() -> new IllegalArgumentException("MatchingRequest not found: " + matchingRequestId));
    }
}
