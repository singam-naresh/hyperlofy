package com.hyperlofy.backend.observability.repository;

import com.hyperlofy.backend.observability.entity.TelemetryEvent;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.UUID;

@Repository
public interface TelemetryEventRepository extends JpaRepository<TelemetryEvent, UUID> {
    List<TelemetryEvent> findByCorrelationId(String correlationId);
    List<TelemetryEvent> findByServiceName(String serviceName);
}
