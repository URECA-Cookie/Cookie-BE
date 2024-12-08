package com.cookie.domain.review.repository;


import com.cookie.domain.review.entity.ReviewComment;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
public interface ReviewCommentRepository extends JpaRepository<ReviewComment, Long> {
    @Query("SELECT rc FROM ReviewComment rc JOIN FETCH rc.user u WHERE rc.review.id = :reviewId ORDER BY rc.updatedAt DESC")
    List<ReviewComment> findCommentsWithUserByReviewId(@Param("reviewId") Long reviewId);

}
