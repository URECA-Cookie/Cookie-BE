package com.cookie.domain.user.repository;

import com.cookie.domain.user.entity.DailyGenreScore;
import org.springframework.data.jpa.repository.JpaRepository;

public interface DailyGenreScoreRepository extends JpaRepository<DailyGenreScore, Long> {
}
