package com.hyperlofy.backend.security.repository;

import com.hyperlofy.backend.security.entity.DataSubjectRequest;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.UUID;

@Repository
public interface DataSubjectRequestRepository extends JpaRepository<DataSubjectRequest, UUID> {
    List<DataSubjectRequest> findByUserId(UUID userId);
}
