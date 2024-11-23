package com.cookie.admin.repository;

import com.cookie.domain.movie.entity.MovieImage;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Modifying;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

public interface MovieImageRepository extends JpaRepository<MovieImage, Long> {

    @Modifying
    @Query("""
        DELETE FROM MovieImage  ma
        WHERE ma.movie.id = :movieId
    """)
    void deleteByMovieId(@Param("movieId") Long movieId);
}
