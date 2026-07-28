package com.hyperlofy.backend.seo.repository;

import com.hyperlofy.backend.seo.entity.SeoStructuredData;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.UUID;

@Repository
public interface SeoStructuredDataRepository extends JpaRepository<SeoStructuredData, UUID> {
    List<SeoStructuredData> findByPage_Id(UUID pageId);
}
