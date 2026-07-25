package com.hyperlofy.backend.inventory.service;

import com.hyperlofy.backend.inventory.dto.InventoryReservationRequest;
import com.hyperlofy.backend.inventory.dto.InventoryReservationResult;
import com.hyperlofy.backend.inventory.entity.Inventory;
import com.hyperlofy.backend.inventory.entity.InventoryReservation;
import com.hyperlofy.backend.inventory.repository.InventoryRepository;
import com.hyperlofy.backend.inventory.repository.InventoryReservationRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.OffsetDateTime;
import java.util.Optional;
import java.util.UUID;

@Service
@RequiredArgsConstructor
public class InventoryReservationService {

    private final InventoryRepository inventoryRepository;
    private final InventoryReservationRepository reservationRepository;

    @Transactional
    public InventoryReservationResult reserveInventory(InventoryReservationRequest req) {
        UUID reservationId = req.getReservationId() == null ? UUID.randomUUID() : req.getReservationId();

        Optional<InventoryReservation> existing = reservationRepository.findById(reservationId);
        if (existing.isPresent()) {
            InventoryReservation r = existing.get();
            if ("RESERVED".equalsIgnoreCase(r.getStatus())) {
                // idempotent
                Inventory inv = inventoryRepository.findByMerchantIdAndProductId(r.getMerchantId(), r.getProductId()).orElse(null);
                int available = inv == null ? 0 : Math.max(0, inv.getAvailableQuantity() - inv.getReservedQuantity());
                return InventoryReservationResult.builder()
                        .reservationId(r.getId())
                        .success(true)
                        .message("Already reserved")
                        .reservedQuantity(r.getQuantity())
                        .availableQuantity(available)
                        .updatedAt(r.getUpdatedAt())
                        .build();
            }
        }

        // find inventory
        Optional<Inventory> invOpt = Optional.empty();
        if (req.getProductId() != null) invOpt = inventoryRepository.findByMerchantIdAndProductId(req.getMerchantId(), req.getProductId());
        if (invOpt.isEmpty() && req.getSku() != null) invOpt = inventoryRepository.findByMerchantIdAndSku(req.getMerchantId(), req.getSku());

        if (invOpt.isEmpty()) {
            return InventoryReservationResult.builder()
                    .reservationId(reservationId)
                    .success(false)
                    .message("Inventory record not found")
                    .reservedQuantity(0)
                    .availableQuantity(0)
                    .build();
        }

        Inventory inv = invOpt.get();
        int available = Math.max(0, inv.getAvailableQuantity() - inv.getReservedQuantity());
        int qty = req.getQuantity() == null ? 0 : req.getQuantity();
        if (qty <= 0) {
            return InventoryReservationResult.builder()
                    .reservationId(reservationId)
                    .success(false)
                    .message("Invalid quantity")
                    .reservedQuantity(0)
                    .availableQuantity(available)
                    .build();
        }

        if (qty > available) {
            return InventoryReservationResult.builder()
                    .reservationId(reservationId)
                    .success(false)
                    .message("Insufficient stock")
                    .reservedQuantity(0)
                    .availableQuantity(available)
                    .build();
        }

        // adjust reserved quantity
        inv.setReservedQuantity(inv.getReservedQuantity() + qty);
        inv.setUpdatedAt(OffsetDateTime.now());
        inventoryRepository.save(inv);

        InventoryReservation reservation = InventoryReservation.builder()
                .id(reservationId)
                .merchantId(inv.getMerchantId())
                .productId(inv.getProductId())
                .sku(inv.getSku())
                .quantity(qty)
                .status("RESERVED")
                .createdAt(OffsetDateTime.now())
                .updatedAt(OffsetDateTime.now())
                .build();

        reservationRepository.save(reservation);

        int postAvailable = Math.max(0, inv.getAvailableQuantity() - inv.getReservedQuantity());

        return InventoryReservationResult.builder()
                .reservationId(reservation.getId())
                .success(true)
                .message("Reserved")
                .reservedQuantity(reservation.getQuantity())
                .availableQuantity(postAvailable)
                .updatedAt(inv.getUpdatedAt())
                .build();
    }

    @Transactional
    public InventoryReservationResult releaseReservation(UUID reservationId) {
        Optional<InventoryReservation> existing = reservationRepository.findById(reservationId);
        if (existing.isEmpty()) {
            return InventoryReservationResult.builder()
                    .reservationId(reservationId)
                    .success(false)
                    .message("Reservation not found")
                    .build();
        }

        InventoryReservation r = existing.get();
        if (!"RESERVED".equalsIgnoreCase(r.getStatus())) {
            return InventoryReservationResult.builder()
                    .reservationId(r.getId())
                    .success(true)
                    .message("Reservation already released or confirmed")
                    .reservedQuantity(r.getQuantity())
                    .build();
        }

        Optional<Inventory> invOpt = Optional.empty();
        if (r.getProductId() != null) invOpt = inventoryRepository.findByMerchantIdAndProductId(r.getMerchantId(), r.getProductId());
        if (invOpt.isEmpty() && r.getSku() != null) invOpt = inventoryRepository.findByMerchantIdAndSku(r.getMerchantId(), r.getSku());

        if (invOpt.isEmpty()) {
            r.setStatus("RELEASED");
            r.setUpdatedAt(OffsetDateTime.now());
            reservationRepository.save(r);
            return InventoryReservationResult.builder()
                    .reservationId(r.getId())
                    .success(false)
                    .message("Inventory record missing during release; reservation marked released")
                    .build();
        }

        Inventory inv = invOpt.get();
        inv.setReservedQuantity(Math.max(0, inv.getReservedQuantity() - r.getQuantity()));
        inv.setUpdatedAt(OffsetDateTime.now());
        inventoryRepository.save(inv);

        r.setStatus("RELEASED");
        r.setUpdatedAt(OffsetDateTime.now());
        reservationRepository.save(r);

        int postAvailable = Math.max(0, inv.getAvailableQuantity() - inv.getReservedQuantity());

        return InventoryReservationResult.builder()
                .reservationId(r.getId())
                .success(true)
                .message("Released")
                .reservedQuantity(r.getQuantity())
                .availableQuantity(postAvailable)
                .updatedAt(inv.getUpdatedAt())
                .build();
    }

    @Transactional
    public InventoryReservationResult confirmReservation(UUID reservationId) {
        Optional<InventoryReservation> existing = reservationRepository.findById(reservationId);
        if (existing.isEmpty()) {
            return InventoryReservationResult.builder()
                    .reservationId(reservationId)
                    .success(false)
                    .message("Reservation not found")
                    .build();
        }

        InventoryReservation r = existing.get();
        if (!"RESERVED".equalsIgnoreCase(r.getStatus())) {
            return InventoryReservationResult.builder()
                    .reservationId(r.getId())
                    .success(true)
                    .message("Reservation already processed")
                    .reservedQuantity(r.getQuantity())
                    .build();
        }

        Optional<Inventory> invOpt = Optional.empty();
        if (r.getProductId() != null) invOpt = inventoryRepository.findByMerchantIdAndProductId(r.getMerchantId(), r.getProductId());
        if (invOpt.isEmpty() && r.getSku() != null) invOpt = inventoryRepository.findByMerchantIdAndSku(r.getMerchantId(), r.getSku());

        if (invOpt.isEmpty()) {
            r.setStatus("CANCELLED");
            r.setUpdatedAt(OffsetDateTime.now());
            reservationRepository.save(r);
            return InventoryReservationResult.builder()
                    .reservationId(r.getId())
                    .success(false)
                    .message("Inventory record missing during confirm; reservation cancelled")
                    .build();
        }

        Inventory inv = invOpt.get();

        // apply final deduction
        int qty = r.getQuantity();
        inv.setReservedQuantity(Math.max(0, inv.getReservedQuantity() - qty));
        inv.setAvailableQuantity(Math.max(0, inv.getAvailableQuantity() - qty));
        inv.setUpdatedAt(OffsetDateTime.now());
        inventoryRepository.save(inv);

        r.setStatus("CONFIRMED");
        r.setUpdatedAt(OffsetDateTime.now());
        reservationRepository.save(r);

        int postAvailable = Math.max(0, inv.getAvailableQuantity() - inv.getReservedQuantity());

        return InventoryReservationResult.builder()
                .reservationId(r.getId())
                .success(true)
                .message("Confirmed")
                .reservedQuantity(r.getQuantity())
                .availableQuantity(postAvailable)
                .updatedAt(inv.getUpdatedAt())
                .build();
    }

    @Transactional
    public InventoryReservationResult cancelReservation(UUID reservationId) {
        // alias to release
        return releaseReservation(reservationId);
    }
}
