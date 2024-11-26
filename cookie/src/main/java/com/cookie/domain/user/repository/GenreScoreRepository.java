package com.cookie.domain.user.repository;

import com.cookie.domain.user.entity.GenreScore;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.repository.query.Param;

import java.util.List;

public interface GenreScoreRepository extends JpaRepository<GenreScore, Long> {
    // 유저 ID로 장르 점수 조회
    List<GenreScore> findAllByUserId(@Param("userId") Long userId);
}
