package com.hyperlofy.backend.experience.repository;

import com.hyperlofy.backend.experience.entity.ReviewReport;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.UUID;

@Repository
public interface ReviewReportRepository extends JpaRepository<ReviewReport, UUID> {
    List<ReviewReport> findByReview_Id(UUID reviewId);
    List<ReviewReport> findByStatus(String status);
}
