package com.hyperlofy.backend.sre.repository;

import com.hyperlofy.backend.sre.entity.PlatformReleaseHistory;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.UUID;

@Repository
public interface PlatformReleaseHistoryRepository extends JpaRepository<PlatformReleaseHistory, UUID> {
    List<PlatformReleaseHistory> findByServiceNameOrderByCreatedAtDesc(String serviceName);
}
