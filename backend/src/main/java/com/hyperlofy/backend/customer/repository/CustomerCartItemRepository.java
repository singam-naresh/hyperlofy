package com.hyperlofy.backend.customer.repository;

import com.hyperlofy.backend.customer.entity.CustomerCartItem;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.Optional;
import java.util.UUID;

@Repository
public interface CustomerCartItemRepository extends JpaRepository<CustomerCartItem, UUID> {
    List<CustomerCartItem> findByCartId(UUID cartId);
    Optional<CustomerCartItem> findByCartIdAndProductId(UUID cartId, UUID productId);
    void deleteByCartId(UUID cartId);
}
