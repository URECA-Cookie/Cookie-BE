package com.cookie.domain.movie.repository;

import com.cookie.domain.movie.entity.MovieActor;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;

import java.util.List;

public interface MovieActorRepository extends JpaRepository<MovieActor, Long> {
    @Query("SELECT ma FROM MovieActor ma JOIN FETCH ma.movie WHERE ma.actor.id = :actorId")
    List<MovieActor> findAllMoviesByActorId(Long actorId);
}
