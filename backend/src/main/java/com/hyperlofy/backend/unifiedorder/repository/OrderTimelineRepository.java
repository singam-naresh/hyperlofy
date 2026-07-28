package com.hyperlofy.backend.unifiedorder.repository;

import com.hyperlofy.backend.unifiedorder.entity.OrderTimeline;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.UUID;

@Repository
public interface OrderTimelineRepository extends JpaRepository<OrderTimeline, UUID> {
    List<OrderTimeline> findByOrderIdOrderByEventTimeAsc(UUID orderId);
}
