package com.hyperlofy.backend.experience.repository;

import com.hyperlofy.backend.experience.entity.ReviewReaction;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.UUID;

@Repository
public interface ReviewReactionRepository extends JpaRepository<ReviewReaction, UUID> {
    List<ReviewReaction> findByReview_Id(UUID reviewId);
}
