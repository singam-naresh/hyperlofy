package com.hyperlofy.backend.security.repository;

import com.hyperlofy.backend.security.entity.RiskRegister;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.Optional;
import java.util.UUID;

@Repository
public interface RiskRegisterRepository extends JpaRepository<RiskRegister, UUID> {
    Optional<RiskRegister> findByRiskCode(String riskCode);
}
