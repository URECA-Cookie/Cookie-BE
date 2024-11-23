package com.cookie.domain.movie.repository;

import com.cookie.domain.movie.entity.Movie;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Modifying;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import java.util.Optional;

@Repository
public interface MovieRepository extends JpaRepository<Movie, Long> {
    // 기본 영화 정보
//    @Query("SELECT m FROM Movie m WHERE m.id = :movieId")
//    Optional<Movie> findById(Long movieId);
    // 기본적으로 제공하는건 여기서 쿼리 짤 필요없음

    // 영화 이미지 가져오기
    @Query("SELECT m FROM Movie m LEFT JOIN FETCH m.movieImages WHERE m.id = :movieId")
    Optional<Movie> findByIdWithImages(Long movieId);

    // 영화 비디오 가져오기
    @Query("SELECT m FROM Movie m LEFT JOIN FETCH m.movieVideos WHERE m.id = :movieId")
    Optional<Movie> findByIdWithVideos(Long movieId);

    // 영화 국가 가져오기
    @Query("SELECT m FROM Movie m LEFT JOIN FETCH m.movieCountries mc LEFT JOIN FETCH mc.country WHERE m.id = :movieId")
    Optional<Movie> findByIdWithCountries(Long movieId);

    @Query("""
        SELECT m
        FROM Movie m
        WHERE m.TMDBMovieId = :TMDBMovieId
    """)
    Optional<Movie> findMovieByTMDBMovieId(@Param("TMDBMovieId") Long TMDBMovieId);

    @Query("""
        SELECT m
        FROM Movie m
        WHERE m.title LIKE CONCAT('%', :title, '%')
    """)
    Page<Movie> findMovieByTitle(@Param("title") String title, Pageable page);

    @Modifying
    @Query("""
        DELETE FROM Movie  m
        WHERE m.id = :movieId
    """)
    void deleteByMovieId(@Param("movieId") Long movieId);
}
