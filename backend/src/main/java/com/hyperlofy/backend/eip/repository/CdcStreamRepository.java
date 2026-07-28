package com.hyperlofy.backend.eip.repository;

import com.hyperlofy.backend.eip.entity.CdcStream;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.Optional;
import java.util.UUID;

@Repository
public interface CdcStreamRepository extends JpaRepository<CdcStream, UUID> {
    Optional<CdcStream> findByStreamName(String streamName);
}
