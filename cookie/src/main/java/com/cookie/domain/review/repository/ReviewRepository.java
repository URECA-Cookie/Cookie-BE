package com.cookie.domain.review.repository;

import com.cookie.domain.movie.entity.Movie;
import com.cookie.domain.review.entity.Review;
import com.cookie.domain.user.entity.User;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.Optional;

@Repository
public interface ReviewRepository extends JpaRepository<Review, Long> {
    Optional<Review> findByUserAndMovie(User user, Movie movie);

    @Query("SELECT r FROM Review r JOIN FETCH r.movie m JOIN FETCH r.user u")
    List<Review> findAllWithMovieAndUser();

}
