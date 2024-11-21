package com.cookie.domain.movie.repository;

import com.cookie.domain.movie.entity.Movie;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;

import java.util.Optional;

public interface MovieRepository extends JpaRepository<Movie, Long> {
    // 기본 영화 정보
    @Query("SELECT m FROM Movie m WHERE m.id = :movieId")
    Optional<Movie> findById(Long movieId);

    // 영화 이미지 가져오기
    @Query("SELECT m FROM Movie m LEFT JOIN FETCH m.movieImages WHERE m.id = :movieId")
    Optional<Movie> findByIdWithImages(Long movieId);

    // 영화 비디오 가져오기
    @Query("SELECT m FROM Movie m LEFT JOIN FETCH m.movieVideos WHERE m.id = :movieId")
    Optional<Movie> findByIdWithVideos(Long movieId);

    // 영화 국가 가져오기
    @Query("SELECT m FROM Movie m LEFT JOIN FETCH m.movieCountries mc LEFT JOIN FETCH mc.country WHERE m.id = :movieId")
    Optional<Movie> findByIdWithCountries(Long movieId);
}
