package com.cookie.domain.movie.repository;

import com.cookie.domain.movie.entity.Movie;
import com.cookie.domain.movie.entity.MovieLike;
import com.cookie.domain.user.entity.User;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

import java.util.List;
import java.util.Optional;

public interface MovieLikeRepository extends JpaRepository<MovieLike, Long> {
    @Query("SELECT ml FROM MovieLike ml JOIN FETCH ml.movie WHERE ml.user.id = :userId")
    List<MovieLike> findAllByUserIdWithMovies(@Param("userId") Long userId);

    Long countByMovieId(Long movieId);

    @Query("SELECT COUNT(ml) > 0 FROM MovieLike ml WHERE ml.movie.id = :movieId AND ml.user.id = :userId")
    boolean isMovieLikedByUser(@Param("movieId") Long movieId, @Param("userId") Long userId);

    Optional<MovieLike> findByMovieAndUser(Movie movie, User user);

    @Query("""
    SELECT ml
    FROM MovieLike ml
    JOIN FETCH ml.user mu
    WHERE ml.movie.id = :movieId
    """)
    List<MovieLike> findAllByMovieId(@Param("movieId") Long movieId);

    @Query("SELECT ml FROM MovieLike ml JOIN FETCH ml.movie m WHERE ml.user.id = :userId")
    Page<MovieLike> findAllByUserIdWithMovies(@Param("userId") Long userId, Pageable pageable);
}
