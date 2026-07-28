package com.hyperlofy.backend.unifiedorder.repository;

import com.hyperlofy.backend.unifiedorder.entity.MasterOrder;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.Optional;
import java.util.UUID;

@Repository
public interface MasterOrderRepository extends JpaRepository<MasterOrder, UUID> {
    Optional<MasterOrder> findByGlobalOrderNumber(String globalOrderNumber);
    Optional<MasterOrder> findByBusinessOrderId(UUID businessOrderId);
    List<MasterOrder> findByCustomerIdOrderByCreatedAtDesc(UUID customerId);
    List<MasterOrder> findByDriverIdOrderByCreatedAtDesc(UUID driverId);
    List<MasterOrder> findByOrderType(String orderType);
}
