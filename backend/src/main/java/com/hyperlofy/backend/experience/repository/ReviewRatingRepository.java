package com.hyperlofy.backend.experience.repository;

import com.hyperlofy.backend.experience.entity.ReviewRating;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.Optional;
import java.util.UUID;

@Repository
public interface ReviewRatingRepository extends JpaRepository<ReviewRating, UUID> {
    Optional<ReviewRating> findByReview_Id(UUID reviewId);
}
