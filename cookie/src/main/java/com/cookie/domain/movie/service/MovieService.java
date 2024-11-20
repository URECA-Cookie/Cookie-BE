package com.cookie.domain.movie.service;


import com.cookie.domain.movie.dto.response.ReviewOfMovieResponse;
import com.cookie.domain.movie.entity.Movie;
import com.cookie.domain.movie.repository.MovieCategoryRepository;
import com.cookie.domain.movie.repository.MovieCountryRepository;
import com.cookie.domain.movie.repository.MovieRepository;
import com.cookie.domain.review.dto.response.MovieReviewResponse;
import com.cookie.domain.review.entity.Review;
import com.cookie.domain.review.repository.ReviewRepository;
import com.cookie.domain.user.dto.response.MovieReviewUserResponse;
import com.cookie.domain.user.entity.User;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;


@Slf4j
@Service
@RequiredArgsConstructor
public class MovieService {
    private final MovieRepository movieRepository;
    private final ReviewRepository reviewRepository;
    private final MovieCountryRepository movieCountryRepository;
    private final MovieCategoryRepository movieCategoryRepository;

    @Transactional(readOnly = true)
    public ReviewOfMovieResponse getMovieReviewList(Long movieId) {
        Movie movie = movieRepository.findById(movieId)
                .orElseThrow(() -> new IllegalArgumentException("not found movieId: " + movieId));

        log.info("Retrieved movie: movieId = {}", movieId);

        List<Review> reviews = reviewRepository.findReviewsByMovieId(movieId);
        log.info("Retrieved {} reviews for movieId = {}", reviews.size(), movieId);

        List<MovieReviewResponse> reviewResponses = reviews.stream()
                .map(review -> {
                    User user = review.getUser();
                    MovieReviewUserResponse userResponse = new MovieReviewUserResponse(
                            user.getNickname(),
                            user.getProfileImage(),
                            user.getMainBadge() != null ? user.getMainBadge().getBadgeImage() : null,
                            user.getMainBadge() != null ? user.getMainBadge().getName() : null
                    );

                    return new MovieReviewResponse(
                            review.getContent(),
                            review.getReviewLike(),
                            review.getMovieScore(),
                            review.getCreatedAt(),
                            review.getUpdatedAt(),
                            userResponse
                    );
                }).toList();

        List<String> subCategories = movieCategoryRepository.findByMovieIdWithCategory(movieId).stream()
                .map(movieCountry -> movieCountry.getCategory().getSubCategory())
                .toList();

        log.info("Categories for movieId = {}: {}", movieId, subCategories);

        List<String> countries = movieCountryRepository.findByMovieIdWithCountry(movieId).stream()
                .map(movieCountry -> movieCountry.getCountry().getCountry())
                .toList();
        log.info("Countries for movieId = {}: {}", movieId, countries);

        return new ReviewOfMovieResponse(
                movie.getTitle(),
                movie.getPoster(),
                movie.getRating().name(),
                movie.getRuntime(),
                subCategories,
                countries,
                movie.getReleasedAt(),
                reviewResponses
        );
    }
}
