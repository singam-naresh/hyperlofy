package com.hyperlofy.backend.agent.repository;

import com.hyperlofy.backend.agent.entity.AgentProfile;
import com.hyperlofy.backend.agent.entity.VerificationStatus;
import jakarta.persistence.LockModeType;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Lock;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.Optional;
import java.util.UUID;

@Repository
public interface AgentRepository extends JpaRepository<AgentProfile, UUID> {
    Optional<AgentProfile> findByUserId(UUID userId);

    @Lock(LockModeType.PESSIMISTIC_WRITE)
    @Query("SELECT a FROM AgentProfile a WHERE a.user.id = :userId")
    Optional<AgentProfile> findByUserIdForUpdate(@Param("userId") UUID userId);

    List<AgentProfile> findByVerificationStatus(VerificationStatus status);
    boolean existsByPanNumber(String panNumber);
    boolean existsByAadhaarNumber(String aadhaarNumber);
}
