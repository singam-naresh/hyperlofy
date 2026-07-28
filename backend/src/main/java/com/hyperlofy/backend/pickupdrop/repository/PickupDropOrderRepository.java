package com.hyperlofy.backend.pickupdrop.repository;

import com.hyperlofy.backend.pickupdrop.entity.PickupDropOrder;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.Optional;
import java.util.UUID;

@Repository
public interface PickupDropOrderRepository extends JpaRepository<PickupDropOrder, UUID> {
    Optional<PickupDropOrder> findByOrderNumber(String orderNumber);
    List<PickupDropOrder> findByCustomerIdOrderByCreatedAtDesc(UUID customerId);
    List<PickupDropOrder> findByAssignedDriverIdOrderByCreatedAtDesc(UUID assignedDriverId);
}
