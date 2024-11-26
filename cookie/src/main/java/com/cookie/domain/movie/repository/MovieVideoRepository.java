package com.cookie.domain.movie.repository;

import com.cookie.domain.movie.entity.MovieVideo;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Modifying;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

public interface MovieVideoRepository extends JpaRepository<MovieVideo, Long> {

    @Modifying
    @Query("""
        DELETE FROM MovieVideo  m
        WHERE m.id = :movieId
    """)
    void deleteByMovieId(@Param("movieId") Long movieId);
}
