package com.cookie.domain.user.repository;

import com.cookie.domain.user.entity.MatchUpParticipation;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.Optional;

@Repository
public interface MatchUpParticipationRepository extends JpaRepository<MatchUpParticipation, Long> {
    boolean existsByUserIdAndMatchUpMovie_Id(Long userId, Long matchUpMovieId);

}
