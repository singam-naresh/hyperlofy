package com.hyperlofy.backend.platform.repository;

import com.hyperlofy.backend.platform.entity.SecretRotationHistory;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.UUID;

@Repository
public interface SecretRotationHistoryRepository extends JpaRepository<SecretRotationHistory, UUID> {
    List<SecretRotationHistory> findBySecretKeyOrderByRotatedAtDesc(String secretKey);
}
