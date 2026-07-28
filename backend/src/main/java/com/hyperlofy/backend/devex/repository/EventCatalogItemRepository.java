package com.hyperlofy.backend.devex.repository;

import com.hyperlofy.backend.devex.entity.EventCatalogItem;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.Optional;
import java.util.UUID;

@Repository
public interface EventCatalogItemRepository extends JpaRepository<EventCatalogItem, UUID> {
    Optional<EventCatalogItem> findByEventName(String eventName);
}
