package com.cookie.domain.movie.repository;

import com.cookie.domain.movie.entity.MovieCategory;
import com.cookie.domain.movie.entity.MovieCountry;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
public interface MovieCategoryRepository extends JpaRepository<MovieCategory, Long> {
    @Query("SELECT mc FROM MovieCategory mc JOIN FETCH mc.category c WHERE mc.movie.id = :movieId")
    List<MovieCategory> findByMovieIdWithCategory(Long movieId);
}
