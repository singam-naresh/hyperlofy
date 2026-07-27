package com.hyperlofy.backend.customer.repository;

import com.hyperlofy.backend.customer.entity.CustomerWishlist;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.Optional;
import java.util.UUID;

@Repository
public interface CustomerWishlistRepository extends JpaRepository<CustomerWishlist, UUID> {
    List<CustomerWishlist> findByUserId(UUID userId);
    Optional<CustomerWishlist> findByUserIdAndProductId(UUID userId, UUID productId);
}
