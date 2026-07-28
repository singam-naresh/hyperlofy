package com.hyperlofy.backend.pickupdrop.repository;

import com.hyperlofy.backend.pickupdrop.entity.PickupDropCustodyHistory;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.UUID;

@Repository
public interface PickupDropCustodyHistoryRepository extends JpaRepository<PickupDropCustodyHistory, UUID> {
    List<PickupDropCustodyHistory> findByOrderIdOrderByCreatedAtDesc(UUID orderId);
}
