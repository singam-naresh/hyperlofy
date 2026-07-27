package com.hyperlofy.backend.customer.repository;

import com.hyperlofy.backend.customer.entity.CustomerCart;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.Optional;
import java.util.UUID;

@Repository
public interface CustomerCartRepository extends JpaRepository<CustomerCart, UUID> {
    Optional<CustomerCart> findByUserId(UUID userId);
}
