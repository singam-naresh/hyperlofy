package com.hyperlofy.backend.marketplace.repository;

import com.hyperlofy.backend.marketplace.entity.InventoryReservation;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.UUID;

@Repository
public interface InventoryReservationRepository extends JpaRepository<InventoryReservation, UUID> {
    List<InventoryReservation> findByCustomerIdAndReservationStatus(UUID customerId, String reservationStatus);
}
