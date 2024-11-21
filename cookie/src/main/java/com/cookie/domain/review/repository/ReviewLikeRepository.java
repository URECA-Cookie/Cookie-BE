package com.cookie.domain.review.repository;

import com.cookie.domain.review.entity.Review;
import com.cookie.domain.review.entity.ReviewLike;
import com.cookie.domain.user.entity.User;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

@Repository
public interface ReviewLikeRepository extends JpaRepository<ReviewLike, Long> {
    ReviewLike findByUserAndReview(User user, Review review);
}
