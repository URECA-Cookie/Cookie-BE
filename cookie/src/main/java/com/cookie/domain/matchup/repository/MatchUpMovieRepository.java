package com.cookie.domain.matchup.repository;

import com.cookie.domain.matchup.entity.MatchUpMovie;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

@Repository
public interface MatchUpMovieRepository extends JpaRepository<MatchUpMovie, Long> {
}
