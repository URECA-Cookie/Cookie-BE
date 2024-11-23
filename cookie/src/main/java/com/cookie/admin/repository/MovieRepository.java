package com.cookie.admin.repository;

import com.cookie.domain.movie.entity.Movie;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Modifying;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

import java.util.Optional;

public interface MovieRepository extends JpaRepository<Movie, Long> {

    @Query("""
        SELECT m
        FROM Movie m
        WHERE m.TMDBMovieId = :TMDBMovieId
    """)
    Optional<Movie> findMovieByTMDBMovieId(@Param("TMDBMovieId") Long TMDBMovieId);

    @Query("""
        SELECT m
        FROM Movie m
        WHERE m.title LIKE CONCAT('%', :title, '%')
    """)
    Page<Movie> findMovieByTitle(@Param("title") String title, Pageable page);

    @Modifying
    @Query("""
        DELETE FROM Movie  m
        WHERE m.id = :movieId
    """)
    void deleteByMovieId(@Param("movieId") Long movieId);
}
