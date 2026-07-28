package com.hyperlofy.backend.engagement.repository;

import com.hyperlofy.backend.engagement.entity.CustomerSegment;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.UUID;

@Repository
public interface CustomerSegmentRepository extends JpaRepository<CustomerSegment, UUID> {
    List<CustomerSegment> findByCustomerId(UUID customerId);
}
