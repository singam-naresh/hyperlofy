package com.hyperlofy.backend.pickupdrop.service;

import com.hyperlofy.backend.pickupdrop.entity.PickupDropCustodyHistory;
import com.hyperlofy.backend.pickupdrop.entity.PickupDropDriverTransfer;
import com.hyperlofy.backend.pickupdrop.entity.PickupDropInsuranceClaim;
import com.hyperlofy.backend.pickupdrop.entity.PickupDropOrder;
import com.hyperlofy.backend.pickupdrop.repository.PickupDropCustodyHistoryRepository;
import com.hyperlofy.backend.pickupdrop.repository.PickupDropDriverTransferRepository;
import com.hyperlofy.backend.pickupdrop.repository.PickupDropInsuranceClaimRepository;
import com.hyperlofy.backend.pickupdrop.repository.PickupDropOrderRepository;
import lombok.RequiredArgsConstructor;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;
import java.util.UUID;

@Service
@RequiredArgsConstructor
public class PickupDropEnterpriseService {

    private static final Logger log = LoggerFactory.getLogger(PickupDropEnterpriseService.class);

    private final PickupDropOrderRepository orderRepository;
    private final PickupDropCustodyHistoryRepository custodyRepository;
    private final PickupDropDriverTransferRepository transferRepository;
    private final PickupDropInsuranceClaimRepository claimRepository;

    @Transactional
    public PickupDropCustodyHistory recordCustodyTransfer(UUID orderId, String custodyEvent, UUID driverId, Double lat, Double lng, String notes) {
        log.info("[PICKUP DROP ENTERPRISE] Custody Event OrderId={}, Event={}, DriverId={}", orderId, custodyEvent, driverId);

        PickupDropCustodyHistory custody = PickupDropCustodyHistory.builder()
                .orderId(orderId)
                .custodyEvent(custodyEvent)
                .handlerDriverId(driverId)
                .gpsLatitude(lat)
                .gpsLongitude(lng)
                .verificationNotes(notes)
                .build();

        return custodyRepository.save(custody);
    }

    @Transactional
    public PickupDropDriverTransfer initiateDriverTransfer(UUID orderId, UUID fromDriverId, UUID toDriverId, String reason) {
        String otp = String.valueOf((int) (Math.random() * 900000) + 100000);
        log.warn("[PICKUP DROP ENTERPRISE] Driver Transfer Initiated OrderId={}, From={}, To={}, Reason={}", orderId, fromDriverId, toDriverId, reason);

        PickupDropDriverTransfer transfer = PickupDropDriverTransfer.builder()
                .orderId(orderId)
                .fromDriverId(fromDriverId)
                .toDriverId(toDriverId)
                .transferReason(reason)
                .transferOtp(otp)
                .isCompleted(false)
                .build();

        return transferRepository.save(transfer);
    }

    @Transactional
    public PickupDropInsuranceClaim submitInsuranceClaim(UUID orderId, String claimType, Double claimedAmount, String description, String evidenceUrl) {
        log.warn("[PICKUP DROP ENTERPRISE] Insurance Claim Submitted OrderId={}, Type={}, Amount={}", orderId, claimType, claimedAmount);

        PickupDropInsuranceClaim claim = PickupDropInsuranceClaim.builder()
                .orderId(orderId)
                .claimType(claimType)
                .claimedAmount(claimedAmount)
                .description(description)
                .evidenceUrl(evidenceUrl)
                .status("SUBMITTED")
                .build();

        return claimRepository.save(claim);
    }

    @Transactional(readOnly = true)
    public List<PickupDropCustodyHistory> getCustodyHistory(UUID orderId) {
        return custodyRepository.findByOrderIdOrderByCreatedAtDesc(orderId);
    }
}
