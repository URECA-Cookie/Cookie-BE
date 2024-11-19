package com.cookie.domain.review.repository;

import com.cookie.domain.movie.entity.Movie;
import com.cookie.domain.review.entity.Review;
import com.cookie.domain.user.entity.User;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.Optional;

@Repository
public interface ReviewRepository extends JpaRepository<Review, Long> {
    Optional<Review> findByUserAndMovie(User user, Movie movie);

}
