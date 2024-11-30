package com.cookie.domain.movie.repository;

import com.cookie.domain.movie.dto.response.MovieSimpleResponse;
import com.cookie.domain.movie.entity.Movie;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Modifying;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.Optional;

@Repository
public interface MovieRepository extends JpaRepository<Movie, Long> {
    // 기본 영화 정보
    @Query("SELECT m FROM Movie m WHERE m.id = :movieId")
    Optional<Movie> findById(@Param("movieId") Long movieId);

    // 영화 이미지 가져오기
    @Query("SELECT m FROM Movie m LEFT JOIN FETCH m.movieImages WHERE m.id = :movieId")
    Optional<Movie> findByIdWithImages(Long movieId);

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

    Page<Movie> findByTitleContainingIgnoreCase(String keyword, Pageable pageable);

    @Query("""
    SELECT new com.cookie.domain.movie.dto.response.MovieSimpleResponse(
        m.id, 
        m.title, 
        m.poster, 
        m.releasedAt, 
        c2.name, 
        CAST((SELECT COUNT(ml) FROM MovieLike ml WHERE ml.movie.id = m.id) AS long), 
        CAST((SELECT COUNT(r) FROM Review r WHERE r.movie.id = m.id) AS long)
    )
    FROM Movie m
    JOIN m.movieCategories mc
    JOIN mc.category c
    JOIN m.country c2
    WHERE c.subCategory = :genre
    ORDER BY m.movieLikes DESC
""")
    List<MovieSimpleResponse> findTop3MoviesByCategory(@Param("genre") String genre);






    @Query("SELECT c.subCategory FROM MovieCategory mc " +
            "JOIN mc.category c " +
            "WHERE mc.movie.id = :movieId AND c.mainCategory = '장르'")
    List<String> findGenresByMovieId(@Param("movieId") Long movieId);

    @Query("SELECT COUNT(ml) FROM MovieLike ml WHERE ml.movie.id = :movieId")
    Long countLikesByMovieId(@Param("movieId") Long movieId);
}

