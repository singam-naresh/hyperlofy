package com.hyperlofy.backend.search.repository;

import com.hyperlofy.backend.search.entity.SearchGovernance;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.Optional;
import java.util.UUID;

@Repository
public interface SearchGovernanceRepository extends JpaRepository<SearchGovernance, UUID> {
    Optional<SearchGovernance> findByDocumentId(String documentId);
}
