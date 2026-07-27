package com.hyperlofy.backend.customer.repository;

import com.hyperlofy.backend.customer.entity.CustomerReview;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.UUID;

@Repository
public interface CustomerReviewRepository extends JpaRepository<CustomerReview, UUID> {
    List<CustomerReview> findByMerchantId(UUID merchantId);
    List<CustomerReview> findByUserId(UUID userId);
}
