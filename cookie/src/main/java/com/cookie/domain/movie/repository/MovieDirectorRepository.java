package com.cookie.domain.movie.repository;

import com.cookie.domain.movie.entity.MovieActor;
import com.cookie.domain.movie.entity.MovieDirector;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Modifying;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

import java.util.List;
import java.util.Optional;

public interface MovieDirectorRepository extends JpaRepository<MovieDirector, Long> {
    @Query("SELECT md FROM MovieDirector md JOIN FETCH md.movie WHERE md.director.id = :directorId")
    List<MovieDirector> findAllMoviesByDirectorId(Long directorId);

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
