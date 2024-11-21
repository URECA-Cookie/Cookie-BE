package com.cookie.domain.review.repository;

import com.cookie.domain.review.entity.ReviewLike;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;

import java.util.List;

public interface ReviewLikeRepository extends JpaRepository<ReviewLike, Long> {
    @Query("SELECT rl FROM ReviewLike rl JOIN FETCH rl.review r WHERE rl.user.id = :userId")
    List<ReviewLike> findAllByUserIdWithReviews(Long userId);
}
