package com.cookie.domain.movie.service;

import com.cookie.domain.movie.entity.Movie;
import com.cookie.domain.movie.repository.MovieRepository;
import com.cookie.domain.review.entity.Review;
import com.cookie.domain.review.repository.ReviewRepository;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;

@Slf4j
@Service
@RequiredArgsConstructor
public class MovieRatingService {
    private final MovieRepository movieRepository;
    private final ReviewRepository reviewRepository;

    @Transactional
    public void updateMovieRatings() {
        List<Movie> movies = movieRepository.findAll();

        int batchSize = 100;
        int totalMovies = movies.size();

        for (int i = 0; i < totalMovies; i += batchSize) {
            int endIndex = Math.min(i + batchSize, totalMovies);
            List<Movie> movieBatch = movies.subList(i, endIndex);

            for (Movie movie : movieBatch) {
                double averageRating = calculateAverageRating(movie);
                movie.updateScore(averageRating);
            }

            movieRepository.saveAll(movieBatch);
        }
    }

    private double calculateAverageRating(Movie movie) {
        List<Review> reviews = reviewRepository.findByMovieId(movie.getId());
        if (reviews.isEmpty()) {
            return 0.0;
        }
        return reviews.stream()
                .mapToInt(Review::getMovieScore)
                .average()
                .orElse(0.0);
    }

}
