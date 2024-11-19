package com.cookie.domain.review.service;

import com.cookie.domain.movie.entity.Movie;
import com.cookie.domain.movie.repository.MovieRepository;
import com.cookie.domain.review.dto.CreateReviewDto;
import com.cookie.domain.review.dto.response.ReviewResponse;
import com.cookie.domain.review.dto.UpdateReviewDto;
import com.cookie.domain.review.entity.Review;
import com.cookie.domain.review.repository.ReviewRepository;
import com.cookie.domain.user.entity.User;
import com.cookie.domain.user.repository.UserRepository;
import jakarta.transaction.Transactional;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;

import java.util.List;

@Slf4j
@Service
@RequiredArgsConstructor
public class ReviewService {
    private final UserRepository userRepository;
    private final MovieRepository movieRepository;
    private final ReviewRepository reviewRepository;

    @Transactional
    public void createReview(Long userId, CreateReviewDto createReviewDto) {
        User user = userRepository.findById(userId)
                .orElseThrow(() -> new IllegalArgumentException("not found userId: " + userId));
        log.info("Retrieved user: userId = {}", userId);

        Long movieId = createReviewDto.getMovieId();
        Movie movie = movieRepository.findById(movieId)
                .orElseThrow(() -> new IllegalArgumentException("not found movieId: " + movieId));
        log.info("Retrieved movie: movieId = {}", movieId);

        if (reviewRepository.findByUserAndMovie(user, movie).isPresent()) {
            throw new IllegalArgumentException("해당 영화에 이미 리뷰를 등록했습니다.");
        }

        Review review = createReviewDto.toEntity(user, movie);
        reviewRepository.save(review);
        log.info("Created review: userId = {}, movieId = {}", userId, movieId);
    }

    @Transactional
    public void updateReview(Long reviewId, UpdateReviewDto updateReviewDto) {
        Review review = reviewRepository.findById(reviewId)
                .orElseThrow(() -> new IllegalArgumentException("not found reviewId: " + reviewId));
        log.info("Retrieved review: reviewId = {}", reviewId);

        review.update(updateReviewDto.getContent(), updateReviewDto.getMovieScore(), updateReviewDto.getIsSpoiler());
        log.info("Updated review: reviewId = {}", reviewId);
    }

    @Transactional
    public List<ReviewResponse> getReviewList() {
        List<Review> reviewList = reviewRepository.findAllWithMovieAndUser();
        log.info("Total reviews: {}", reviewList.size());

        return reviewList.stream()
                .map(ReviewResponse::fromEntity)
                .toList();

    }
}

