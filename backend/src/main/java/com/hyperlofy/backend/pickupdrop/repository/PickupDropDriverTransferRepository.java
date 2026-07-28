package com.hyperlofy.backend.pickupdrop.repository;

import com.hyperlofy.backend.pickupdrop.entity.PickupDropDriverTransfer;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.UUID;

@Repository
public interface PickupDropDriverTransferRepository extends JpaRepository<PickupDropDriverTransfer, UUID> {
    List<PickupDropDriverTransfer> findByOrderIdOrderByCreatedAtDesc(UUID orderId);
}
