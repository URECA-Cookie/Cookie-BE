package com.cookie.domain.movie.repository;

import com.cookie.domain.movie.entity.MovieMonthRanking;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Modifying;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

public interface MovieMonthRankingRepository extends JpaRepository<MovieMonthRanking, Integer> {

    @Modifying
    @Query("""
        DELETE FROM MovieMonthRanking mmr
        WHERE mmr.movie.id = :movieId
    """)
    void deleteByMovieId(@Param("movieId") Long movieId);
}
