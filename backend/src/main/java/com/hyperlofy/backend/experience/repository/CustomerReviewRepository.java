package com.hyperlofy.backend.experience.repository;

import com.hyperlofy.backend.experience.entity.CustomerReview;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.Optional;
import java.util.UUID;

@Repository
public interface CustomerReviewRepository extends JpaRepository<CustomerReview, UUID> {
    Optional<CustomerReview> findByReviewCode(String reviewCode);
    List<CustomerReview> findByProductId(UUID productId);
    List<CustomerReview> findByMerchantId(UUID merchantId);
    List<CustomerReview> findByCustomerId(UUID customerId);
    List<CustomerReview> findByStatus(String status);
}
