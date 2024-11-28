package com.cookie.domain.matchup.repository;

import com.cookie.domain.matchup.entity.MatchUp;
import com.cookie.domain.matchup.entity.enums.MatchUpStatus;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.Optional;

@Repository
public interface MatchUpRepository extends JpaRepository<MatchUp, Long> {
    @Query("SELECT m FROM MatchUp m JOIN FETCH m.movie1 JOIN FETCH m.movie2 WHERE m.status = :status")
    List<MatchUp> findByStatusWithMovies(@Param("status") MatchUpStatus status);

    @Query("""
            SELECT m FROM MatchUp m
            JOIN FETCH m.movie1 movie1
            JOIN FETCH movie1.charmPoint cp1
            JOIN FETCH movie1.emotionPoint ep1
            JOIN FETCH m.movie2 movie2
            JOIN FETCH movie2.charmPoint cp2
            JOIN FETCH movie2.emotionPoint ep2
            WHERE m.id = :matchUpId""")
    Optional<MatchUp> findMatchUpWithMoviesAndPoints(@Param("matchUpId") Long matchUpId);

    List<MatchUp> findByStatus(MatchUpStatus status);

    List<MatchUp> findTop2ByStatusOrderByEndAtDesc(MatchUpStatus status);


}
