package com.cookie.domain.movie.repository;

import com.cookie.domain.movie.entity.MovieLike;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;

import java.util.List;

public interface MovieLikeRepository extends JpaRepository<MovieLike, Long> {
    @Query("SELECT ml FROM MovieLike ml JOIN FETCH ml.movie WHERE ml.user.id = :userId")
    List<MovieLike> findAllByUserIdWithMovies(Long userId);
}
