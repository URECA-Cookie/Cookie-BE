package com.cookie.admin.repository;

import com.cookie.admin.entity.AdminMovieRecommend;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Modifying;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

public interface RecommendRepository extends JpaRepository<AdminMovieRecommend, Long> {

    @Modifying
    @Query("""
        DELETE FROM AdminMovieRecommend amr
        WHERE amr.movie.id = :movieId
    """)
    void deleteByMovieId(@Param("movieId") Long movieId);
}
