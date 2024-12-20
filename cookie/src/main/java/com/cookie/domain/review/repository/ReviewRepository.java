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
    @Query("SELECT r FROM Review r WHERE r.user = :user AND r.movie = :movie")
    Optional<Review> findByUserAndMovie(@Param("user") User user, @Param("movie") Movie movie);

    @Query("SELECT r FROM Review r JOIN FETCH r.movie m JOIN FETCH r.user u LEFT JOIN FETCH u.userBadges ub LEFT JOIN FETCH ub.badge WHERE r.isHide = false ORDER BY r.createdAt DESC")
    Page<Review> findAllWithMovieAndUser(Pageable pageable);

    @Query("SELECT r FROM Review r JOIN FETCH r.movie m JOIN FETCH r.user u LEFT JOIN FETCH u.userBadges ub LEFT JOIN FETCH ub.badge b WHERE r.isSpoiler = true AND r.isHide = false ORDER BY r.createdAt DESC")
    Page<Review> findAllWithMovieAndUserWithSpoilers(Pageable pageable);

    @Query("SELECT r FROM Review r JOIN FETCH r.user u LEFT JOIN FETCH u.userBadges ub LEFT JOIN FETCH ub.badge b WHERE r.movie.id = :movieId AND r.isHide = false ORDER BY r.createdAt DESC")
    Page<Review> findReviewsByMovieId(Long movieId, Pageable pageable);

    @Query("SELECT r FROM Review r JOIN FETCH r.user u LEFT JOIN FETCH u.userBadges ub LEFT JOIN FETCH ub.badge b WHERE r.movie.id = :movieId AND r.isSpoiler = true AND r.isHide = false ORDER BY r.createdAt DESC")
    Page<Review> findSpoilerReviewsByMovieId(Long movieId, Pageable pageable);

    @Query("SELECT r FROM Review r JOIN FETCH r.user u LEFT JOIN FETCH u.userBadges ub LEFT JOIN FETCH ub.badge b WHERE r.movie.id = :movieId AND r.isHide = false ORDER BY r.reviewLike DESC")
    Page<Review> findMostLikedReviewsByMovieId(Long movieId, Pageable pageable);

    @Query("SELECT r FROM Review r JOIN FETCH r.user u LEFT JOIN FETCH u.userBadges ub LEFT JOIN FETCH ub.badge b WHERE r.movie.id = :movieId AND r.isSpoiler = true AND r.isHide = false ORDER BY r.reviewLike DESC")
    Page<Review> findMostLikedSpoilerReviewsByMovieId(Long movieId, Pageable pageable);


    @Query("SELECT r FROM Review r JOIN FETCH r.movie WHERE r.user.id = :userId ORDER BY r.createdAt DESC")
    List<Review> findAllByUserIdWithMovie(Long userId);

    @Query("""
        SELECT r
        FROM Review r
        JOIN FETCH r.user u
        WHERE r.movie.id = :movieId
        ORDER BY r.createdAt DESC
    """)
    List<Review> findReviewsByMovieId(@Param("movieId") Long movieId);

    Long countByMovieId(Long movieId);

    List<Review> findByMovieId(Long movieId);


    @Modifying
    @Query("""
        DELETE FROM Review r
        WHERE r.movie.id = :movieId
    """)
    void deleteByMovieId(@Param("movieId") Long movieId);

    @Query("SELECT r FROM Review r JOIN FETCH r.movie WHERE r.user.id = :userId ORDER BY r.createdAt DESC")
    Page<Review> findAllByUserId(Long userId, Pageable pageable);

}

