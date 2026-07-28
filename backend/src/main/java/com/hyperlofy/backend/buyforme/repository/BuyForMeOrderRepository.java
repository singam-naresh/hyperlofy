package com.hyperlofy.backend.buyforme.repository;

import com.hyperlofy.backend.buyforme.entity.BuyForMeOrder;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.Optional;
import java.util.UUID;

@Repository
public interface BuyForMeOrderRepository extends JpaRepository<BuyForMeOrder, UUID> {
    Optional<BuyForMeOrder> findByOrderNumber(String orderNumber);
    List<BuyForMeOrder> findByCustomerIdOrderByCreatedAtDesc(UUID customerId);
    List<BuyForMeOrder> findByAssignedDriverIdOrderByCreatedAtDesc(UUID assignedDriverId);
    List<BuyForMeOrder> findByStatus(String status);
}
