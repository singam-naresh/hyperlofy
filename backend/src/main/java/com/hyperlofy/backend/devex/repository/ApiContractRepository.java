package com.hyperlofy.backend.devex.repository;

import com.hyperlofy.backend.devex.entity.ApiContract;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.Optional;
import java.util.UUID;

@Repository
public interface ApiContractRepository extends JpaRepository<ApiContract, UUID> {
    Optional<ApiContract> findByContractName(String contractName);
}
