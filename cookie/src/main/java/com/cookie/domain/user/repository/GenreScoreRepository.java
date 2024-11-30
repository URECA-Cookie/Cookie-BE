package com.cookie.domain.user.repository;

import com.cookie.domain.user.dto.response.GenreScoreResponse;
import com.cookie.domain.user.entity.GenreScore;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

import java.util.List;

public interface GenreScoreRepository extends JpaRepository<GenreScore, Long> {
    // 유저 ID로 장르 점수 조회
    List<GenreScore> findAllByUserId(@Param("userId") Long userId);

    @Query(""" 
    SELECT new com.cookie.domain.user.dto.response.GenreScoreResponse(
           gs.romance, gs.horror, gs.comedy, gs.action, gs.fantasy, gs.animation,
           gs.crime, gs.sf, gs.music, gs.thriller, gs.war, gs.documentary,
           gs.drama, gs.family, gs.history, gs.mistery, gs.tvMovie, gs.western, gs.adventure
    )
    FROM GenreScore gs
    WHERE gs.user.id = :userId
""")
    GenreScoreResponse findGenreScoresByUserId(@Param("userId") Long userId);

}
