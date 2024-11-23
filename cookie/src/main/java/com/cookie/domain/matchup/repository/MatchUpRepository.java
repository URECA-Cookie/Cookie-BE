package com.cookie.domain.matchup.repository;

import com.cookie.domain.matchup.entity.MatchUp;
import com.cookie.domain.matchup.entity.enums.MatchUpStatus;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
public interface MatchUpRepository extends JpaRepository<MatchUp, Long> {
    @Query("SELECT m FROM MatchUp m JOIN FETCH m.movie1 JOIN FETCH m.movie2 WHERE m.status = :status")
    List<MatchUp> findByStatusWithMovies(@Param("status") MatchUpStatus status);
}
