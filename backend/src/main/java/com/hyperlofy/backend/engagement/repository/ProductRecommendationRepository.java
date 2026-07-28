package com.hyperlofy.backend.engagement.repository;

import com.hyperlofy.backend.engagement.entity.ProductRecommendation;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.Optional;
import java.util.UUID;

@Repository
public interface ProductRecommendationRepository extends JpaRepository<ProductRecommendation, UUID> {
    Optional<ProductRecommendation> findByRecommendationCode(String recommendationCode);
    List<ProductRecommendation> findByCustomerId(UUID customerId);
}
