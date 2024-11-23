package com.cookie.admin.repository;

import com.cookie.domain.director.entity.Director;
import com.cookie.domain.movie.entity.MovieDirector;
import com.cookie.domain.movie.entity.MovieImage;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Modifying;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

import java.util.Optional;

public interface MovieDirectorRepository extends JpaRepository<MovieDirector, Long> {

    @Query("""
        SELECT md
        FROM MovieDirector md
        WHERE md.movie.id = :movieId
    """)
    Optional<MovieDirector> findMovieDirectorByMovieId(Long movieId);

    @Modifying
    @Query("""
        DELETE FROM MovieDirector  md
        WHERE md.movie.id = :movieId
    """)
    void deleteByMovieId(@Param("movieId") Long movieId);

}
