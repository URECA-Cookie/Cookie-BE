package com.cookie.domain.review.repository;

import com.cookie.domain.review.entity.ReviewLike;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import com.cookie.domain.review.entity.Review;
import com.cookie.domain.user.entity.User;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.Map;

@Repository
public interface ReviewLikeRepository extends JpaRepository<ReviewLike, Long> {
    @Query("SELECT rl FROM ReviewLike rl JOIN FETCH rl.review r WHERE rl.user.id = :userId")
    List<ReviewLike> findAllByUserIdWithReviews(Long userId);
    
    ReviewLike findByUserAndReview(User user, Review review);

    @Query("SELECT COUNT(rl) > 0 FROM ReviewLike rl WHERE rl.review.id = :reviewId AND rl.user.id = :userId")
    boolean existsByReviewIdAndUserId(@Param("reviewId") Long reviewId, @Param("userId") Long userId);

}
