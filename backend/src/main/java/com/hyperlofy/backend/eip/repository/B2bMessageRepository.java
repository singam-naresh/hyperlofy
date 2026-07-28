package com.hyperlofy.backend.eip.repository;

import com.hyperlofy.backend.eip.entity.B2bMessage;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.Optional;
import java.util.UUID;

@Repository
public interface B2bMessageRepository extends JpaRepository<B2bMessage, UUID> {
    Optional<B2bMessage> findByControlNumber(String controlNumber);
    List<B2bMessage> findByPartnerId(UUID partnerId);
}
