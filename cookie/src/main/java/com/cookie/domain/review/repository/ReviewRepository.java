package com.cookie.domain.review.repository;

import com.cookie.domain.review.entity.Review;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;

import java.util.List;

public interface ReviewRepository extends JpaRepository<Review, Long> {
    // 특정 유저의 리뷰와 연관된 영화 정보를 가져오기
    @Query("SELECT r FROM Review r JOIN FETCH r.movie WHERE r.user.id = :userId")
    List<Review> findAllByUserIdWithMovie(Long userId);
}
