package com.hyperlofy.backend.ai.verify.repository;

import com.hyperlofy.backend.ai.verify.VerifyEntity;
import com.hyperlofy.backend.ai.verify.VerificationType;
import com.hyperlofy.backend.ai.verify.VerificationResult;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.UUID;

@Repository
public interface VerifyRepository extends JpaRepository<VerifyEntity, UUID> {
    List<VerifyEntity> findByOrder_IdAndActiveTrue(UUID orderId);
    List<VerifyEntity> findByVerificationTypeAndVerificationResult(VerificationType type, VerificationResult result);
}
