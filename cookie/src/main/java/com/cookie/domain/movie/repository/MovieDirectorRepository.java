package com.cookie.domain.movie.repository;

import com.cookie.domain.movie.entity.MovieActor;
import com.cookie.domain.movie.entity.MovieDirector;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;

import java.util.List;

public interface MovieDirectorRepository extends JpaRepository<MovieDirector, Long> {
    @Query("SELECT md FROM MovieDirector md JOIN FETCH md.movie WHERE md.director.id = :directorId")
    List<MovieDirector> findAllMoviesByDirectorId(Long directorId);
}
