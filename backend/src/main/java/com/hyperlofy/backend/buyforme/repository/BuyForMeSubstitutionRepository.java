package com.hyperlofy.backend.buyforme.repository;

import com.hyperlofy.backend.buyforme.entity.BuyForMeSubstitution;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.UUID;

@Repository
public interface BuyForMeSubstitutionRepository extends JpaRepository<BuyForMeSubstitution, UUID> {
    List<BuyForMeSubstitution> findByOrderIdOrderByCreatedAtDesc(UUID orderId);
}
