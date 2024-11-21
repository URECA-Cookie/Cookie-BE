package com.cookie.domain.review.repository;

import com.cookie.domain.review.entity.ReviewComment;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;

import java.util.List;

public interface ReviewCommentRepository extends JpaRepository<ReviewComment, Long> {
    @Query("SELECT rc FROM ReviewComment rc " +
            "JOIN FETCH rc.review r " +
            "JOIN FETCH r.movie m " +
            "WHERE rc.user.id = :userId")
    List<ReviewComment> findAllByUserIdWithReviewAndMovie(Long userId);
}
