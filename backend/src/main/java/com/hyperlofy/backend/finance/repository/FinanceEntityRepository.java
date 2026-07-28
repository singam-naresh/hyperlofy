package com.hyperlofy.backend.finance.repository;

import com.hyperlofy.backend.finance.entity.FinanceEntity;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.Optional;
import java.util.UUID;

@Repository
public interface FinanceEntityRepository extends JpaRepository<FinanceEntity, UUID> {
    Optional<FinanceEntity> findByEntityCode(String entityCode);
}
