package com.cookie.domain.review.repository;

import com.cookie.domain.review.entity.Review;
import com.cookie.domain.movie.entity.Movie;
import com.cookie.domain.user.entity.User;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Modifying;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.Optional;

@Repository
public interface ReviewRepository extends JpaRepository<Review, Long> {
    Optional<Review> findByUserAndMovie(User user, Movie movie);

    @Query("SELECT r FROM Review r JOIN FETCH r.movie m JOIN FETCH r.user u LEFT JOIN FETCH u.userBadges ub LEFT JOIN FETCH ub.badge WHERE r.isHide = false ORDER BY r.updatedAt DESC")
    Page<Review> findAllWithMovieAndUser(Pageable pageable);

    @Query("SELECT r FROM Review r JOIN FETCH r.movie m JOIN FETCH r.user u LEFT JOIN FETCH u.userBadges ub LEFT JOIN FETCH ub.badge b WHERE r.isSpoiler = true AND r.isHide = false ORDER BY r.updatedAt DESC")
    Page<Review> findAllWithMovieAndUserWithSpoilers(Pageable pageable);

    @Query("SELECT r FROM Review r JOIN FETCH r.user u LEFT JOIN FETCH u.userBadges ub LEFT JOIN FETCH ub.badge b WHERE r.movie.id = :movieId AND r.isHide = false")
    Page<Review> findReviewsByMovieId(Long movieId, Pageable pageable);

    @Query("SELECT r FROM Review r JOIN FETCH r.user u LEFT JOIN FETCH u.userBadges ub LEFT JOIN FETCH ub.badge b WHERE r.movie.id = :movieId AND r.isSpoiler = true AND r.isHide = false")
    Page<Review> findSpoilerReviewsByMovieId(Long movieId, Pageable pageable);
  
    // 특정 유저의 리뷰와 연관된 영화 정보를 가져오기
    @Query("SELECT r FROM Review r JOIN FETCH r.movie WHERE r.user.id = :userId")
    List<Review> findAllByUserIdWithMovie(Long userId);

    @Query("""
        SELECT r
        FROM Review r
        JOIN FETCH r.user u
        WHERE r.movie.id = :movieId
    """)
    List<Review> findReviewsByMovieId(@Param("movieId") Long movieId);

    Long countByMovieId(Long movieId);

    @Modifying
    @Query("""
        DELETE FROM Review r
        WHERE r.movie.id = :movieId
    """)
    void deleteByMovieId(@Param("movieId") Long movieId);
}

