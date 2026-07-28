package com.hyperlofy.backend.buyforme.repository;

import com.hyperlofy.backend.buyforme.entity.BuyForMePurchaseProof;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.Optional;
import java.util.UUID;

@Repository
public interface BuyForMePurchaseProofRepository extends JpaRepository<BuyForMePurchaseProof, UUID> {
    Optional<BuyForMePurchaseProof> findByOrderId(UUID orderId);
}
