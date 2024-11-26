package com.cookie.domain.movie.repository;

import com.cookie.domain.movie.entity.Movie;
import com.cookie.domain.movie.entity.MovieCategory;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Modifying;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
public interface MovieCategoryRepository extends JpaRepository<MovieCategory, Long> {
    @Query("SELECT mc FROM MovieCategory mc JOIN FETCH mc.category c WHERE mc.movie.id = :movieId")
    List<MovieCategory> findByMovieIdWithCategory(Long movieId);

    @Query("""
        SELECT mc.category.id
        FROM MovieCategory mc
        WHERE mc.movie.id = :movieId
    """)
    List<Long> findCategoriesById(@Param("movieId") Long movieId);

    @Query("""
        SELECT mc
        FROM MovieCategory mc
        WHERE mc.movie.id = :movieId
    """)
    List<MovieCategory> findMovieCategoriesById(@Param("movieId") Long movieId);

    @Modifying
    @Query("""
        DELETE FROM MovieCategory  mc
        WHERE mc.movie.id = :movieId
        AND mc.category.id = :categoryId
    """)
    void deleteByMovieIdAndCategoryId(@Param("movieId") Long movieId, @Param("categoryId") Long categoryId);

    @Modifying
    @Query("""
        DELETE FROM MovieCategory  mc
        WHERE mc.movie.id = :movieId
    """)
    void deleteByMovieId(@Param("movieId") Long movieId);

    @Query("SELECT mc.movie FROM MovieCategory mc WHERE mc.category.id = :categoryId")
    List<Movie> findMoviesByCategoryId(@Param("categoryId") Long categoryId);
}
