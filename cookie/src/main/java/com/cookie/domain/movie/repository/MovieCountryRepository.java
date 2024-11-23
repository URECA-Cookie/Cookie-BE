package com.cookie.domain.movie.repository;

import com.cookie.domain.movie.entity.MovieCountry;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Modifying;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
public interface MovieCountryRepository extends JpaRepository<MovieCountry, Long> {
    @Query("SELECT mc FROM MovieCountry mc JOIN FETCH mc.country c WHERE mc.movie.id = :movieId")
    List<MovieCountry> findByMovieIdWithCountry(Long movieId);

    @Modifying
    @Query("""
        DELETE FROM MovieCountry  ma
        WHERE ma.movie.id = :movieId
    """)
    void deleteByMovieId(@Param("movieId") Long movieId);
}
